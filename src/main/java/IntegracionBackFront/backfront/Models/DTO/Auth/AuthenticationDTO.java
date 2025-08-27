package IntegracionBackFront.backfront.Models.DTO.Auth;

import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString @EqualsAndHashCode
public class AuthenticationDTO {

    @NotBlank(message = "Para iniciar sesión el correo es requerido.")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;
    @NotBlank(message = "Para iniciar sesión la contraseña es requerida")
    private String contrasena;
    private String nombre;
    private String apellido;
    private String rol;
}
