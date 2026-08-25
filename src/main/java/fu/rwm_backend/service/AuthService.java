package fu.rwm_backend.service;

import fu.rwm_backend.dto.LoginRequest;
import fu.rwm_backend.dto.LoginResponse;
import fu.rwm_backend.dto.UserSummaryResponse;
import java.util.UUID;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserSummaryResponse currentUser(UUID userId);
}
