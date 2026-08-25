package fu.he182575.rwm_backend.common.exception;

public class UnauthenticatedException extends ApiException {

    public UnauthenticatedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
