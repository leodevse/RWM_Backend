package fu.he182575.rwm_backend.security;

import fu.he182575.rwm_backend.entity.UserEntity;

public interface JwtTokenService {

    String issueAccessToken(UserEntity user);

    JwtClaims parseAndValidate(String token);
}
