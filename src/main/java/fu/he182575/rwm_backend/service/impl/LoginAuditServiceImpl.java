package fu.he182575.rwm_backend.service.impl;

import fu.he182575.rwm_backend.common.enums.LoginFailureReason;
import fu.he182575.rwm_backend.common.enums.LoginOutcome;
import fu.he182575.rwm_backend.entity.LoginAuditEntity;
import fu.he182575.rwm_backend.repository.LoginAuditRepository;
import fu.he182575.rwm_backend.service.LoginAuditService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAuditServiceImpl implements LoginAuditService {

    private final LoginAuditRepository loginAuditRepository;

    public LoginAuditServiceImpl(LoginAuditRepository loginAuditRepository) {
        this.loginAuditRepository = loginAuditRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String loginIdentifier, UUID userId, LoginOutcome outcome, LoginFailureReason failureReason) {
        LoginAuditEntity audit = new LoginAuditEntity();
        audit.setLoginIdentifier(loginIdentifier);
        audit.setUserId(userId);
        audit.setOutcome(outcome);
        audit.setFailureReason(failureReason);
        loginAuditRepository.save(audit);
    }
}
