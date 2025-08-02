package IntegracionBackFront.backfront.Exceptions.Users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionUserDontInsert extends RuntimeException {
    public ExceptionUserDontInsert(String message) {
        super(message);
    }
}
