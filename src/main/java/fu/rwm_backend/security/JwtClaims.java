package fu.rwm_backend.security;

import fu.rwm_backend.common.enums.UserRole;
import java.time.Instant;
import java.util.UUID;

public record JwtClaims(
        UUID userId,
        String loginIdentifier,
        String fullName,
        UserRole role,
        Instant issuedAt,
        Instant expiresAt
) {
}
