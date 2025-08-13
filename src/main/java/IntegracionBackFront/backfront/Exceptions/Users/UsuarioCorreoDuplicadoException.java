package IntegracionBackFront.backfront.Exceptions.Users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsuarioCorreoDuplicadoException extends RuntimeException {
    public UsuarioCorreoDuplicadoException(String message) {
        super(message);
    }
}
