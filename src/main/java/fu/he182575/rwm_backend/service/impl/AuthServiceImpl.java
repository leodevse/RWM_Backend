package fu.he182575.rwm_backend.service.impl;

import fu.he182575.rwm_backend.common.enums.AccountStatus;
import fu.he182575.rwm_backend.common.enums.LoginFailureReason;
import fu.he182575.rwm_backend.common.enums.LoginOutcome;
import fu.he182575.rwm_backend.common.enums.UserRole;
import fu.he182575.rwm_backend.common.exception.AccountDisabledException;
import fu.he182575.rwm_backend.common.exception.AuthenticationFailedException;
import fu.he182575.rwm_backend.common.exception.InvalidRoleException;
import fu.he182575.rwm_backend.dto.LoginRequest;
import fu.he182575.rwm_backend.dto.LoginResponse;
import fu.he182575.rwm_backend.entity.UserEntity;
import fu.he182575.rwm_backend.mapper.AuthMapper;
import fu.he182575.rwm_backend.repository.UserRepository;
import fu.he182575.rwm_backend.security.JwtClaims;
import fu.he182575.rwm_backend.security.JwtTokenService;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements fu.he182575.rwm_backend.service.AuthService {

    private final UserRepository userRepository;
    private final fu.he182575.rwm_backend.service.LoginAuditService loginAuditService;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            fu.he182575.rwm_backend.service.LoginAuditService loginAuditService,
            JwtTokenService jwtTokenService,
            PasswordEncoder passwordEncoder,
            AuthMapper authMapper
    ) {
        this.userRepository = userRepository;
        this.loginAuditService = loginAuditService;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByLoginIdentifierIgnoreCase(request.loginIdentifier())
                .orElse(null);

        if (user == null) {
            recordAudit(request.loginIdentifier(), null, LoginOutcome.FAILURE, LoginFailureReason.INVALID_CREDENTIALS);
            throw new AuthenticationFailedException("Invalid login credentials");
        }

        if (!isAllowedRole(user.getRole())) {
            recordAudit(request.loginIdentifier(), user.getId(), LoginOutcome.FAILURE, LoginFailureReason.INVALID_ROLE);
            throw new InvalidRoleException("Account is not allowed to login");
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            recordAudit(request.loginIdentifier(), user.getId(), LoginOutcome.FAILURE, LoginFailureReason.DISABLED_ACCOUNT);
            throw new AccountDisabledException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            recordAudit(request.loginIdentifier(), user.getId(), LoginOutcome.FAILURE, LoginFailureReason.INVALID_CREDENTIALS);
            throw new AuthenticationFailedException("Invalid login credentials");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenService.issueAccessToken(user);
        JwtClaims claims = jwtTokenService.parseAndValidate(token);

        recordAudit(request.loginIdentifier(), user.getId(), LoginOutcome.SUCCESS, null);
        return authMapper.toLoginResponse(token, claims.expiresAt(), user);
    }

    private boolean isAllowedRole(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.STAFF;
    }

    private void recordAudit(String loginIdentifier, java.util.UUID userId, LoginOutcome outcome, LoginFailureReason failureReason) {
        loginAuditService.record(loginIdentifier, userId, outcome, failureReason);
    }
}
