package IntegracionBackFront.backfront.Controller.Users;

import IntegracionBackFront.backfront.Exceptions.Users.ExceptionUserDontInsert;
import IntegracionBackFront.backfront.Models.ApiResponse.ApiResponse;
import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import IntegracionBackFront.backfront.Services.Users.UserServices;
import com.google.protobuf.Api;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServices service;

    /**
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/getDataUsers")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> obtenerUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        if (size <= 0 || size > 50){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "La paginación de datos debe estar entre 1 y 50"
            ));
            return ResponseEntity.ok(null);
        }
        Page<UserDTO> users = service.getAllUsers(page, size);
        if (users == null){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "Error al obtener los datos"
            ));
        }
        return ResponseEntity.ok(ApiResponse.success("Datos consultados correctamente", users));
    }

    /**
     *
     * @param json
     * @return
     */
    @PostMapping("/newUser")
    public ResponseEntity<ApiResponse<UserDTO>> insertarUsuario(@Valid @RequestBody UserDTO json){
        if (json == null){
            throw new ExceptionUserDontInsert("Error al recibir y procesar la información del usuario");
        }
        UserDTO usuarioGuardado = service.createUser(json);
        if (usuarioGuardado == null){
            throw new ExceptionUserDontInsert("El usuario no pudo ser registrado debido a algun inconveniente con los datos");
        }
        return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente", usuarioGuardado));
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @Valid
            @PathVariable Long id,
            @RequestBody UserDTO json,
            BindingResult bindingResult)
    {
            if (bindingResult.hasErrors()){
                Map<String, String> errores = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errores.put(error.getField(), error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(errores);
            }

            try{
                UserDTO usuarioActualizado = service.updateUser(id, json);
                return ResponseEntity.ok(usuarioActualizado);
            }catch (Exception e){
                return ResponseEntity.badRequest().body("Error al modificar el usuario");
            }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(
            @PathVariable Long id
    ){
        try{
            if (!service.deleteUser(id)){
                //La eliminacion no se pudo realizar
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Error", "El usuario no encontrado")
                        .body(Map.of(
                                "Error", "NOT FOUND",
                                "Mensjae", "El usuario no fue encontrado",
                                "Fecha y hora", Instant.now().toString()
                        ));
            }
            //La eliminacion si se ejecuto correctamente
            return ResponseEntity.ok().body(Map.of(
                    "status", "Proceso completado",
                    "mensaje", "Usuario eliminado exitosamente"
            ));
        }catch (Exception e){
            // Si ocurre cualquier error inesperado, retorna 500 (Internal Server Error)
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error",  // Indicador de error
                    "message", "Error al eliminar el usuario",  // Mensaje general
                    "detail", e.getMessage()  // Detalles técnicos del error (para debugging)
            ));
        }
    }

    @PutMapping("/update/{id}/password")
    private ResponseEntity<Map<String, Object>> resetPassword(@Valid @PathVariable Long id){
        try{
            boolean respuesta = service.resetPassword(id);
            if (respuesta){
                return ResponseEntity.ok().body(Map.of(
                        "Success", "Proceso completado exitosamente",
                        "Message", "La contrasena fue restablecida correctamente"
                ));
            }
            return ResponseEntity.ok().body(Map.of(
                    "Status", "Error",
                    "Message", "El proceso no pudo ser completado"
            ));
        }catch (Exception e){
            return ResponseEntity.ok().body(Map.of(
                    "Status", "Proceso interrumpido",
                    "Message", "El proceso no pudo ser completado"
            ));
        }
    }
}
