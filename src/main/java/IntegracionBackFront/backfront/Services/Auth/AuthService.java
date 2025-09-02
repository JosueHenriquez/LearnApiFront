package IntegracionBackFront.backfront.Services.Auth;

import IntegracionBackFront.backfront.Config.Crypto.Argon2Password;
import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import IntegracionBackFront.backfront.Repositories.Auth.AuthRepository;
import IntegracionBackFront.backfront.Repositories.Users.UserRepository;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    public boolean Login(String correo, String contrasena){
        Argon2Password objHash = new Argon2Password();
        Optional<UserEntity> list = repo.findByCorreo(correo).stream().findFirst();
        if (list.isPresent()){
            UserEntity usuario = list.get();
            String nombreTipoUsuario = usuario.getTipoUsuario().getNombreTipo();
            System.out.println("Usuario encontrado ID: " + usuario.getId() +
                                ", email: " + usuario.getCorreo() +
                                ", rol: " + nombreTipoUsuario);
            String HashDB = usuario.getContrasena();
            boolean verificado = objHash.VerifyPassword(HashDB, contrasena);
            return verificado;
        }
        return false;
    }

    public Optional<UserEntity> obtenerUsuario(String email){
        // Buscar usuario completo en la base de datos
        Optional<UserEntity> userOpt = repo.findByCorreo(email);
        return (userOpt != null) ? userOpt : null;
    }
}
