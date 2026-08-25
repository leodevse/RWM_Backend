package fu.he182575.rwm_backend.common.exception;

public class AccessDeniedException extends ApiException {

    public AccessDeniedException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
