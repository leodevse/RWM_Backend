package fu.he182575.rwm_backend.controller;

import fu.he182575.rwm_backend.common.enums.AccountStatus;
import fu.he182575.rwm_backend.common.enums.UserRole;
import fu.he182575.rwm_backend.entity.UserEntity;
import fu.he182575.rwm_backend.repository.LoginAuditRepository;
import fu.he182575.rwm_backend.repository.UserRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAuditRepository loginAuditRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @AfterEach
    void tearDown() {
        loginAuditRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void login_shouldReturnSessionForValidAdmin() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("admin01");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Admin User");
        user.setRole(UserRole.ADMIN);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        HttpResponse<String> response = postJson("{\"loginIdentifier\":\"admin01\",\"password\":\"Password123\"}");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"tokenType\":\"Bearer\""));
        assertTrue(response.body().contains("\"role\":\"ADMIN\""));
        assertTrue(response.body().contains("\"loginIdentifier\":\"admin01\""));
    }

    @Test
    void login_shouldReturnValidationErrorWhenPasswordMissing() throws Exception {
        HttpResponse<String> response = postJson("{\"loginIdentifier\":\"admin01\"}");

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("\"code\":\"VALIDATION_ERROR\""));
    }

    @Test
    void login_shouldReturnUnauthorizedForWrongPassword() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("staff01");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Staff User");
        user.setRole(UserRole.STAFF);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        HttpResponse<String> response = postJson("{\"loginIdentifier\":\"staff01\",\"password\":\"WrongPassword\"}");

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("\"code\":\"UNAUTHORIZED\""));
        assertEquals(1, loginAuditRepository.count());
    }

    @Test
    void login_shouldReturnUnauthorizedForDisabledAccount() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("disabled01");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Disabled User");
        user.setRole(UserRole.STAFF);
        user.setAccountStatus(AccountStatus.DISABLED);
        userRepository.save(user);

        HttpResponse<String> response = postJson("{\"loginIdentifier\":\"disabled01\",\"password\":\"Password123\"}");

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("\"code\":\"UNAUTHORIZED\""));
        assertEquals(1, loginAuditRepository.count());
    }

    @Test
    void adminOnlyEndpoint_shouldRejectUnauthenticatedRequest() throws Exception {
        HttpResponse<String> response = get("/api/v1/admin/ping");

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("\"code\":\"UNAUTHORIZED\""));
    }

    @Test
    void adminOnlyEndpoint_shouldRejectStaffRequest() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("staff-admin-check");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Staff User");
        user.setRole(UserRole.STAFF);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        String token = loginAndExtractToken("staff-admin-check", "Password123");
        HttpResponse<String> response = get("/api/v1/admin/ping", "Bearer " + token);

        assertEquals(403, response.statusCode());
        assertTrue(response.body().contains("\"code\":\"FORBIDDEN\""));
    }

    @Test
    void loginResponse_shouldExposeOpenApiRelevantFields() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("docs01");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Docs User");
        user.setRole(UserRole.ADMIN);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        HttpResponse<String> response = postJson("{\"loginIdentifier\":\"docs01\",\"password\":\"Password123\"}");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"accessToken\""));
        assertTrue(response.body().contains("\"tokenType\":\"Bearer\""));
        assertTrue(response.body().contains("\"expiresAt\""));
    }

    private HttpResponse<String> postJson(String body) throws Exception {
        return postJson(body, null);
    }

    private HttpResponse<String> postJson(String body, String authorization) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/auth/login"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        if (authorization != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authorization);
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return get(path, null);
    }

    private HttpResponse<String> get(String path, String authorization) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET();
        if (authorization != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authorization);
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private String loginAndExtractToken(String loginIdentifier, String password) throws Exception {
        HttpResponse<String> response = postJson(
                "{\"loginIdentifier\":\"" + loginIdentifier + "\",\"password\":\"" + password + "\"}"
        );
        String body = response.body();
        int start = body.indexOf("\"accessToken\":\"");
        if (start < 0) {
            throw new IllegalStateException("accessToken not found in response: " + body);
        }
        start += "\"accessToken\":\"".length();
        int end = body.indexOf('"', start);
        if (end < 0) {
            throw new IllegalStateException("accessToken closing quote not found in response: " + body);
        }
        return body.substring(start, end);
    }
}
