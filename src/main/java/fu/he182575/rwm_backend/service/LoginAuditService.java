package fu.he182575.rwm_backend.service;

import fu.he182575.rwm_backend.common.enums.LoginFailureReason;
import fu.he182575.rwm_backend.common.enums.LoginOutcome;
import java.util.UUID;

public interface LoginAuditService {

    void record(String loginIdentifier, UUID userId, LoginOutcome outcome, LoginFailureReason failureReason);
}
