package fu.he182575.rwm_backend.dto;

import fu.he182575.rwm_backend.common.enums.UserRole;
import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String loginIdentifier,
        String fullName,
        UserRole role
) {
}
