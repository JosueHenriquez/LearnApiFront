package IntegracionBackFront.backfront.Models.DTO.UserType;

import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString @EqualsAndHashCode
public class UserTypeDTO {

    private Long id;
    private String nombreTipo;
    private String descripcion;
}
