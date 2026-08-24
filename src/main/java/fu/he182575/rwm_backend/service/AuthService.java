package fu.he182575.rwm_backend.service;

import fu.he182575.rwm_backend.dto.LoginRequest;
import fu.he182575.rwm_backend.dto.LoginResponse;
import fu.he182575.rwm_backend.dto.UserSummaryResponse;
import java.util.UUID;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserSummaryResponse currentUser(UUID userId);
}
