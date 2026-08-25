package fu.rwm_backend.common.exception;

public class InternalErrorException extends ApiException {

    public InternalErrorException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, message);
        initCause(cause);
    }
}
