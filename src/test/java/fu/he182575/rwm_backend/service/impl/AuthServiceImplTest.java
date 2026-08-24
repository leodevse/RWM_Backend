package fu.he182575.rwm_backend.service.impl;

import fu.he182575.rwm_backend.common.enums.AccountStatus;
import fu.he182575.rwm_backend.common.enums.LoginFailureReason;
import fu.he182575.rwm_backend.common.enums.LoginOutcome;
import fu.he182575.rwm_backend.common.enums.UserRole;
import fu.he182575.rwm_backend.common.exception.AccountDisabledException;
import fu.he182575.rwm_backend.common.exception.AuthenticationFailedException;
import fu.he182575.rwm_backend.common.exception.InvalidRoleException;
import fu.he182575.rwm_backend.dto.LoginRequest;
import fu.he182575.rwm_backend.entity.UserEntity;
import fu.he182575.rwm_backend.mapper.AuthMapper;
import fu.he182575.rwm_backend.repository.UserRepository;
import fu.he182575.rwm_backend.security.JwtClaims;
import fu.he182575.rwm_backend.security.JwtTokenService;
import fu.he182575.rwm_backend.service.LoginAuditService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoginAuditService loginAuditService;
    @Mock
    private JwtTokenService jwtTokenService;
    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12);
        authService = new AuthServiceImpl(userRepository, loginAuditService, jwtTokenService, passwordEncoder, new AuthMapper());
    }

    @Test
    void login_shouldReturnTokenAndRecordSuccessAudit() {
        UUID userId = UUID.randomUUID();
        String rawPassword = "Password123";
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setLoginIdentifier("admin01");
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFullName("Admin User");
        user.setRole(UserRole.ADMIN);
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findByLoginIdentifierIgnoreCase("admin01")).thenReturn(Optional.of(user));
        when(jwtTokenService.issueAccessToken(user)).thenReturn("header.payload.signature");
        when(jwtTokenService.parseAndValidate("header.payload.signature")).thenReturn(
                new JwtClaims(userId, "admin01", "Admin User", UserRole.ADMIN, Instant.now(), Instant.now().plusSeconds(3600))
        );

        var response = authService.login(new LoginRequest("admin01", rawPassword));

        assertEquals("header.payload.signature", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(userId, response.user().id());
        assertEquals(UserRole.ADMIN, response.user().role());
        assertNotNull(response.expiresAt());
        verify(loginAuditService).record("admin01", userId, LoginOutcome.SUCCESS, null);
    }

    @Test
    void login_shouldRejectInvalidCredentialsAndRecordFailureAudit() {
        when(userRepository.findByLoginIdentifierIgnoreCase("missing")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class,
                () -> authService.login(new LoginRequest("missing", "Password123")));

        verify(loginAuditService).record("missing", null, LoginOutcome.FAILURE, LoginFailureReason.INVALID_CREDENTIALS);
    }

    @Test
    void login_shouldRejectDisabledAccountAndRecordFailureAudit() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("staff01");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Staff User");
        user.setRole(UserRole.STAFF);
        user.setAccountStatus(AccountStatus.DISABLED);

        when(userRepository.findByLoginIdentifierIgnoreCase("staff01")).thenReturn(Optional.of(user));

        assertThrows(AccountDisabledException.class,
                () -> authService.login(new LoginRequest("staff01", "Password123")));

        verify(loginAuditService).record("staff01", user.getId(), LoginOutcome.FAILURE, LoginFailureReason.DISABLED_ACCOUNT);
    }

    @Test
    void login_shouldRejectInvalidRoleAndRecordFailureAudit() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("legacy01");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Legacy User");
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findByLoginIdentifierIgnoreCase("legacy01")).thenReturn(Optional.of(user));

        assertThrows(InvalidRoleException.class,
                () -> authService.login(new LoginRequest("legacy01", "Password123")));

        verify(loginAuditService).record("legacy01", user.getId(), LoginOutcome.FAILURE, LoginFailureReason.INVALID_ROLE);
    }
}
