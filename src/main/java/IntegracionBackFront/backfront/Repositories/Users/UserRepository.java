package IntegracionBackFront.backfront.Repositories.Users;

import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAll(Pageable pageable);
    boolean existsByCorreo(String email);
    Optional<UserEntity> findByCorreo(String email);
}
