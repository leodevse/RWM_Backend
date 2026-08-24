package fu.he182575.rwm_backend.service;

import fu.he182575.rwm_backend.dto.LoginRequest;
import fu.he182575.rwm_backend.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
