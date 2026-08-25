package fu.rwm_backend.service;

import fu.rwm_backend.common.enums.LoginFailureReason;
import fu.rwm_backend.common.enums.LoginOutcome;
import java.util.UUID;

public interface LoginAuditService {

    void record(String loginIdentifier, UUID userId, LoginOutcome outcome, LoginFailureReason failureReason);
}
