package IntegracionBackFront.backfront.Services.Users;

import IntegracionBackFront.backfront.Entities.UserType.UserTypeEntity;
import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import IntegracionBackFront.backfront.Exceptions.UserType.TipoUsuarioNotFound;
import IntegracionBackFront.backfront.Exceptions.Users.UserNotFoundException;
import IntegracionBackFront.backfront.Exceptions.Users.UsuarioCorreoDuplicadoException;
import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import IntegracionBackFront.backfront.Repositories.UserType.UserTypeRepository;
import IntegracionBackFront.backfront.Repositories.Users.UserRepository;
import IntegracionBackFront.backfront.Config.Crypto.Argon2Password;
import IntegracionBackFront.backfront.Utils.PasswordGenerator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServices {

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserTypeRepository repoUserType;

    @Autowired
    private Argon2Password argon2;

    /**
     *
     * @param page
     * @param size
     * @return retorna los datos paginados conforme al tamano establecido por pagina
     */
    public Page<UserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> pageEntity = repo.findAll(pageable);
        return pageEntity.map(this::ConvertirADTO);
    }

    /**
     *
     * @param json
     * @return
     */
    public UserDTO createUser(@Valid UserDTO json) {
        if (verificarExistenciaUsuario(json.getCorreo())){
            throw new UsuarioCorreoDuplicadoException("El correo ya esta registrado en la base de datos");
        }
        UserEntity objEntity = ConvertirAEntity(json);
        UserEntity savedUser = repo.save(objEntity);
        return ConvertirADTO(savedUser);
    }

    /**
     *
     * @param id
     * @param json
     * @return
     */
    public UserDTO updateUser(@Valid Long id, UserDTO json) {
        //1. Verificar existencia
        UserEntity existencia = repo.findById(id).orElseThrow(()-> new UserNotFoundException("Usuario no encontrado"));
        if (!existencia.getCorreo().equals(json.getCorreo())){
            if (verificarExistenciaUsuario(json.getCorreo())){
                throw new UsuarioCorreoDuplicadoException("El correo que pretende registrar ya existe en la base de datos");
            }
        }
        //2.Actualizar valores
        existencia.setNombre(json.getNombre());
        existencia.setApellido(json.getApellido());
        existencia.setCorreo(json.getCorreo());
        if (json.getIdTipoUsuario() != null){
            UserTypeEntity tipoUsuario = repoUserType.findById(json.getIdTipoUsuario())
                    .orElseThrow(()-> new TipoUsuarioNotFound("Tipo de usuario no encontrado"));
            existencia.setTipoUsuario(tipoUsuario);
        }else {
            existencia.setTipoUsuario(null);
        }
        UserEntity usuarioActualizado = repo.save(existencia);
        return ConvertirADTO(usuarioActualizado);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean deleteUser(Long id) {
        UserEntity existente = repo.findById(id).orElse(null);
        if (existente!=null){
            repo.deleteById(id);
            return true;
        }else {
            log.error("Usuario no encontrado");
            return false;
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean resetPassword(@Valid Long id) {
        UserEntity existente = repo.findById(id).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (existente != null){
            String newPassword = PasswordGenerator.generateSecurePassword(12);
            existente.setContrasena(argon2.EncryptPassword(newPassword));
            UserEntity usuarioActualizado = repo.save(existente);
            return true;
        }
        return false;
    }

    // ***************** METODOS COMPLEMENTARIOS **********************
    public boolean verificarExistenciaUsuario(String email){
        return repo.existsByCorreo(email);
    }

    /**
     * El metodo recibira un objeto Entity y pasara cada uno de los valores de Entity -> DTO
     * @param userEntity
     * @return valores en un tipo de objeto DTO
     */
    private UserDTO ConvertirADTO(UserEntity userEntity) {
        UserDTO dto = new UserDTO();
        dto.setId(userEntity.getId());
        dto.setNombre(userEntity.getNombre());
        dto.setApellido(userEntity.getApellido());
        dto.setCorreo(userEntity.getCorreo());
        dto.setContrasena(userEntity.getContrasena());
        dto.setFechaRegistro(userEntity.getFechaRegistro());
        if (userEntity.getTipoUsuario() != null){
            dto.setNombreTipoUsuario(userEntity.getTipoUsuario().getNombreTipo());
            dto.setIdTipoUsuario(userEntity.getTipoUsuario().getId());
        }else{
            dto.setNombreTipoUsuario("Sin tipo de usuario asignado");
            dto.setId(null);
        }
        return dto;
    }

    /**
     * El metodo recibira un objeto DTO el cual se llamara json y pasara cada uno de los valores de
     * DTO -> Entity
     * @param json
     * @return valores en un tipo de objeto Entity
     */
    private UserEntity ConvertirAEntity(@Valid UserDTO json) {
        Argon2Password objHash = new Argon2Password();
        UserEntity entity = new UserEntity();
        entity.setNombre(json.getNombre());
        entity.setApellido(json.getApellido());
        entity.setCorreo(json.getCorreo());
        entity.setContrasena(argon2.EncryptPassword(json.getContrasena()));
        entity.setFechaRegistro(json.getFechaRegistro());
        if (json.getIdTipoUsuario() != null){
            UserTypeEntity entityType = repoUserType.findById(json.getIdTipoUsuario())
                    .orElseThrow(()-> new TipoUsuarioNotFound("ID de Tipo de usuario no encontrado"));
            entity.setTipoUsuario(entityType);
        }
        return entity;
    }

}
