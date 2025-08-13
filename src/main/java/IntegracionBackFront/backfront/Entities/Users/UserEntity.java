package IntegracionBackFront.backfront.Entities.Users;

import IntegracionBackFront.backfront.Entities.UserType.UserTypeEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter @Setter
@ToString @EqualsAndHashCode
@Table(name = "USUARIO")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(name = "seq_usuario", sequenceName = "seq_usuario", allocationSize = 1)
    @Column(name = "IDUSUARIO")
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "APELLIDO")
    private String apellido;

    @Column(name = "EMAIL")
    private String correo;

    @Column(name = "CONTRASENA")
    private String contrasena;

    @Column(name = "FECHAREGISTRO")
    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "IDTIPOUSUARIO", referencedColumnName = "IDTIPOUSUARIO")
    private UserTypeEntity tipoUsuario;
}
