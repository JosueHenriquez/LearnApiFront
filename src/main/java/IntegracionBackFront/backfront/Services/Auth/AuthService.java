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
        System.out.println("Método login");
        Argon2Password objHash = new Argon2Password();
        Optional<UserEntity> list = repo.findByCorreo(correo).stream().findFirst();
        if (list.isPresent()){
            UserEntity usuario = list.get();
            System.out.println("Usuario encontrado ID: " + usuario.getId() + ", email: " + usuario.getCorreo());
            String HashDB = usuario.getContrasena();
            boolean verificado = objHash.VerifyPassword(HashDB, contrasena);
            System.out.println("Verificación de contraseña: " + verificado);
            return verificado;
        }
        return false;
    }
}
