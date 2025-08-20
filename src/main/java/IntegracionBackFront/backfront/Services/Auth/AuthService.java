package IntegracionBackFront.backfront.Services.Auth;

import IntegracionBackFront.backfront.Config.Crypto.Argon2Password;
import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import IntegracionBackFront.backfront.Repositories.Auth.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthRepository repo;

    public boolean Login(String correo, String contrasena){
        Argon2Password objHash = new Argon2Password();
        Optional<UserEntity> usuario = repo.findByCorreo(correo);
        if (usuario.isPresent()){
            String HashDB = usuario.get().getContrasena();
            return objHash.VerifyPassword(HashDB, contrasena);
        }
        return false;
    }
}
