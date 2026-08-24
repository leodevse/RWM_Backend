package fu.he182575.rwm_backend.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UserSummaryResponse user
) {
}
