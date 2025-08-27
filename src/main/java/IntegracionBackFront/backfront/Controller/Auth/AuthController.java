package IntegracionBackFront.backfront.Controller.Auth;

import IntegracionBackFront.backfront.Models.DTO.Auth.AuthenticationDTO;
import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import IntegracionBackFront.backfront.Services.Auth.AuthService;
import IntegracionBackFront.backfront.Utils.JWT.JWTUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService service;
    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/login")
    private ResponseEntity<String> login (@Valid @RequestBody UserDTO data, HttpServletRequest request, HttpServletResponse response){
        System.out.println("Método controller");
        //1. Se valida que los datos no estén vacíos
        if ((data.getCorreo().isEmpty() || data.getCorreo().isBlank() ||
            data.getContrasena().isEmpty() || data.getContrasena().isBlank())){
            /**
             * Se intenta iniciar sesión enviando el correo y la contraseña, en caso los valores
             * sean incorrectos retonará un FALSE.
             */
            return ResponseEntity.status(401). body("Error, verifica que hayas compartido todas las credenciales necesarias para iniciar sesión");
        }
        //System.out.println(data.getCorreo() + " " + data.getContrasena());
        if (service.Login(data.getCorreo(), data.getContrasena())){
            addTokenCookie(response, data);
            return ResponseEntity.ok("Inicio de sesión exitoso");
        }
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    /**
     * Se genera el token y se guarda en la Cookie
     * @param response
     * @param data
     */
    private void addTokenCookie(HttpServletResponse response, @Valid UserDTO data) {
        String token = jwtUtils.create(
                String.valueOf(data.getId()),
                data.getCorreo(),
                data.getNombreTipoUsuario());
        //Crear la cookie
        Cookie cookie = new Cookie("authToken", token); //Nombre y valor de la cookie
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400); //Tiempo definido en segundos
        response.addCookie(cookie);
    }
}
