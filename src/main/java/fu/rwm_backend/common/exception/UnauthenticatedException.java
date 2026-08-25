package fu.rwm_backend.common.exception;

public class UnauthenticatedException extends ApiException {

    public UnauthenticatedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
