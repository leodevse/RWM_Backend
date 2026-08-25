package fu.he182575.rwm_backend.common.exception;

public class AccountDisabledException extends ApiException {

    public AccountDisabledException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
