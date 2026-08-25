package fu.rwm_backend.security;

import fu.rwm_backend.entity.UserEntity;

public interface JwtTokenService {

    String issueAccessToken(UserEntity user);

    JwtClaims parseAndValidate(String token);
}
