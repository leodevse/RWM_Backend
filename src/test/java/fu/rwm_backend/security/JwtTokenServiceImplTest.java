package fu.rwm_backend.security;

import fu.rwm_backend.common.enums.AccountStatus;
import fu.rwm_backend.common.enums.UserRole;
import fu.rwm_backend.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenServiceImplTest {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void issueAndParseToken_shouldRoundTripPrincipalData() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("admin01");
        user.setFullName("Admin User");
        user.setRole(UserRole.ADMIN);
        user.setAccountStatus(AccountStatus.ACTIVE);

        String token = jwtTokenService.issueAccessToken(user);
        JwtClaims claims = jwtTokenService.parseAndValidate(token);

        assertEquals(user.getId(), claims.userId());
        assertEquals(user.getLoginIdentifier(), claims.loginIdentifier());
        assertEquals(user.getFullName(), claims.fullName());
        assertEquals(user.getRole(), claims.role());
        assertTrue(Duration.between(claims.issuedAt(), claims.expiresAt()).toMinutes() >= 59);
    }

    @Test
    void parseAndValidate_shouldRejectInvalidIssuer() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("admin01");
        user.setFullName("Admin User");
        user.setRole(UserRole.ADMIN);
        user.setAccountStatus(AccountStatus.ACTIVE);

        JwtProperties issuingProperties = new JwtProperties();
        issuingProperties.setIssuer("wrong-issuer");
        issuingProperties.setSecret("test-only-super-secret-key-for-jwt-validation");
        issuingProperties.setExpirationMinutes(60);
        String token = new JwtTokenServiceImpl(issuingProperties).issueAccessToken(user);

        assertThrows(JwtTokenException.class, () -> jwtTokenService.parseAndValidate(token));
    }
}
