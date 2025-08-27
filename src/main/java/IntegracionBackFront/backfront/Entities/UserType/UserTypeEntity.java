package IntegracionBackFront.backfront.Entities.UserType;

import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@EqualsAndHashCode
@Table(name = "TIPOUSUARIO")
public class UserTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tipousuario")
    @SequenceGenerator(name = "seq_tipousuario", sequenceName = "seq_tipousuario", allocationSize = 1)
    @Column(name = "IDTIPOUSUARIO")
    private Long id;

    @Column(name = "NOMBRETIPO")
    private String nombreTipo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @OneToMany(mappedBy = "tipoUsuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserEntity> usuarios = new ArrayList<>();

    @Override
    public String toString() {
        return "UserTypeEntity{" +
                "descripcion='" + descripcion + '\'' +
                ", id=" + id +
                ", nombreTipo='" + nombreTipo + '\'' +
                ", usuarios=" + usuarios +
                '}';
    }
}
