package fu.he182575.rwm_backend.mapper;

import fu.he182575.rwm_backend.dto.LoginResponse;
import fu.he182575.rwm_backend.dto.UserSummaryResponse;
import fu.he182575.rwm_backend.entity.UserEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public LoginResponse toLoginResponse(String token, Instant expiresAt, UserEntity user) {
        return new LoginResponse(
                token,
                "Bearer",
                expiresAt,
                new UserSummaryResponse(
                        user.getId(),
                        user.getLoginIdentifier(),
                        user.getFullName(),
                        user.getRole()
                )
        );
    }

    public UserSummaryResponse toUserSummary(UserEntity user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getLoginIdentifier(),
                user.getFullName(),
                user.getRole()
        );
    }
}
