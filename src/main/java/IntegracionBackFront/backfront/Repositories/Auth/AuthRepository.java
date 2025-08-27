package IntegracionBackFront.backfront.Repositories.Auth;

import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCorreo(String correo);
}
