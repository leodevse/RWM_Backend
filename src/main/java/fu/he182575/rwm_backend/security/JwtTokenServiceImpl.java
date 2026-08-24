package fu.he182575.rwm_backend.security;

import fu.he182575.rwm_backend.common.enums.UserRole;
import fu.he182575.rwm_backend.entity.UserEntity;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final JwtProperties jwtProperties;

    public JwtTokenServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String issueAccessToken(UserEntity user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtProperties.getExpirationMinutes() * 60);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId().toString());
        payload.put("login_identifier", user.getLoginIdentifier());
        payload.put("full_name", user.getFullName());
        payload.put("role", user.getRole().name());
        payload.put("iss", jwtProperties.getIssuer());
        payload.put("iat", issuedAt.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String header = encode(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
        String body = encode(toJson(payload).getBytes(StandardCharsets.UTF_8));
        return header + "." + body + "." + sign(header, body);
    }

    @Override
    public JwtClaims parseAndValidate(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtTokenException("Token is empty");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new JwtTokenException("Token format is invalid");
        }

        String expectedSignature = sign(parts[0], parts[1]);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new JwtTokenException("Token signature is invalid");
        }

        String payloadJson = new String(decode(parts[1]), StandardCharsets.UTF_8);
        String issuer = readString(payloadJson, "iss");
        if (!jwtProperties.getIssuer().equals(issuer)) {
            throw new JwtTokenException("Token issuer is invalid");
        }

        Instant expiresAt = Instant.ofEpochSecond(readLong(payloadJson, "exp"));
        if (Instant.now().isAfter(expiresAt)) {
            throw new JwtTokenException("Token is expired");
        }

        return new JwtClaims(
                UUID.fromString(readString(payloadJson, "sub")),
                readString(payloadJson, "login_identifier"),
                readNullableString(payloadJson, "full_name"),
                UserRole.valueOf(readString(payloadJson, "role")),
                Instant.ofEpochSecond(readLong(payloadJson, "iat")),
                expiresAt
        );
    }

    private String sign(String header, String body) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] signature = mac.doFinal((header + "." + body).getBytes(StandardCharsets.UTF_8));
            return encode(signature);
        } catch (NoSuchAlgorithmException | java.security.InvalidKeyException ex) {
            throw new JwtTokenException("Unable to sign JWT", ex);
        }
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    private String toJson(Map<String, Object> payload) {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append('"').append(escape(entry.getKey())).append('"').append(':');
            Object value = entry.getValue();
            if (value == null) {
                builder.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                builder.append(value);
            } else {
                builder.append('"').append(escape(String.valueOf(value))).append('"');
            }
        }
        builder.append('}');
        return builder.toString();
    }

    private String readString(String json, String key) {
        String value = readNullableString(json, key);
        if (value == null) {
            throw new JwtTokenException("Token payload missing " + key);
        }
        return value;
    }

    private String readNullableString(String json, String key) {
        String raw = readRawValue(json, key);
        if ("null".equals(raw)) {
            return null;
        }
        if (raw.length() >= 2 && raw.startsWith("\"") && raw.endsWith("\"")) {
            return unescape(raw.substring(1, raw.length() - 1));
        }
        return raw;
    }

    private long readLong(String json, String key) {
        String raw = readRawValue(json, key);
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            throw new JwtTokenException("Token payload field " + key + " is invalid", ex);
        }
    }

    private String readRawValue(String json, String key) {
        String quotedKey = "\"" + escape(key) + "\"";
        int keyIndex = json.indexOf(quotedKey);
        if (keyIndex < 0) {
            throw new JwtTokenException("Token payload missing " + key);
        }
        int colonIndex = json.indexOf(':', keyIndex + quotedKey.length());
        if (colonIndex < 0) {
            throw new JwtTokenException("Token payload missing separator for " + key);
        }
        int valueStart = colonIndex + 1;
        int valueEnd = valueStart;
        boolean inString = false;
        boolean escaped = false;
        if (valueStart < json.length() && json.charAt(valueStart) == '"') {
            inString = true;
            valueEnd++;
            while (valueEnd < json.length()) {
                char current = json.charAt(valueEnd);
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    valueEnd++;
                    break;
                }
                valueEnd++;
            }
        } else {
            while (valueEnd < json.length() && json.charAt(valueEnd) != ','
                    && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }
        }
        if (inString && valueEnd > json.length()) {
            throw new JwtTokenException("Token payload field " + key + " is malformed");
        }
        return json.substring(valueStart, valueEnd).trim();
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (escaped) {
                builder.append(current);
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else {
                builder.append(current);
            }
        }
        return builder.toString();
    }
}
