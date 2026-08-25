package fu.he182575.rwm_backend.common.exception;

import fu.he182575.rwm_backend.dto.ApiErrorResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldMessage)
                .findFirst()
                .orElse("Validation failed");
        return build(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        return build(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex) {
        return build(ErrorCode.INTERNAL_ERROR, "Internal server error");
    }

    private ResponseEntity<ApiErrorResponse> build(ErrorCode errorCode, String message) {
        ApiErrorResponse body = new ApiErrorResponse(
                new ApiErrorResponse.ErrorItem(errorCode.getCode(), message, Instant.now())
        );
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    private String toFieldMessage(FieldError error) {
        return error.getDefaultMessage() != null ? error.getDefaultMessage() : error.getField() + " is invalid";
    }
}
