package fu.he182575.rwm_backend.common.exception;

public class InvalidRoleException extends ApiException {

    public InvalidRoleException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
