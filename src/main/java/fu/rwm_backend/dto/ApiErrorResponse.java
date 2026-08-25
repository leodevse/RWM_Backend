package fu.he182575.rwm_backend.dto;

import java.time.Instant;

public record ApiErrorResponse(
        ErrorItem error
) {
    public record ErrorItem(
            String code,
            String message,
            Instant timestamp
    ) {
    }
}
