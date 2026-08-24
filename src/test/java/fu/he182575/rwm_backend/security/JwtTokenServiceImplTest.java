package fu.he182575.rwm_backend.security;

import fu.he182575.rwm_backend.common.enums.AccountStatus;
import fu.he182575.rwm_backend.common.enums.UserRole;
import fu.he182575.rwm_backend.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
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
}
