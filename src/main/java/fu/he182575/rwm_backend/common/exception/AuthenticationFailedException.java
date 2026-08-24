package fu.he182575.rwm_backend.common.exception;

public class AuthenticationFailedException extends ApiException {

    public AuthenticationFailedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
