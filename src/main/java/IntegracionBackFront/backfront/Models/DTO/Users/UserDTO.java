package IntegracionBackFront.backfront.Models.DTO.Users;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDTO {

    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    @Email(message = "El correo debe tener un formato valido")
    private String correo;
    @Size(min = 8, message = "La contrasena debe ser un minimo de 8 caracteres")
    private String contrasena;
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fechaRegistro;
    @Positive(message = "El valor proporcionado debe ser positivo y valido")
    private Long idTipoUsuario;

    //Campo adicional
    private String nombreTipoUsuario;
}
