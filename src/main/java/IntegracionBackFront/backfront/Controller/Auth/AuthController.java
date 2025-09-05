package IntegracionBackFront.backfront.Controller.Auth;

import IntegracionBackFront.backfront.Entities.Users.UserEntity;
import IntegracionBackFront.backfront.Models.DTO.Auth.AuthenticationDTO;
import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import IntegracionBackFront.backfront.Services.Auth.AuthService;
import IntegracionBackFront.backfront.Utils.JWT.JWTUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService service;
    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/login")
    private ResponseEntity<String> login(@RequestBody UserDTO data, HttpServletResponse response) {
        if (data.getCorreo() == null || data.getCorreo().isBlank() ||
                data.getContrasena() == null || data.getContrasena().isBlank()) {
            return ResponseEntity.status(401).body("Error: Credenciales incompletas");
        }

        if (service.Login(data.getCorreo(), data.getContrasena())) {
            addTokenCookie(response, data.getCorreo()); // ← Pasar solo el correo
            return ResponseEntity.ok("Inicio de sesión exitoso");
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    /**
     * Se genera el token y se guarda en la Cookie
     * @param response
     * @param
     */
    private void addTokenCookie(HttpServletResponse response, String correo) {
        // Obtener el usuario completo de la base de datos
        Optional<UserEntity> userOpt = service.obtenerUsuario(correo);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            String token = jwtUtils.create(
                    String.valueOf(user.getId()),
                    user.getCorreo(),
                    user.getTipoUsuario().getNombreTipo() // ← Usar el nombre real del tipo
            );

            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "No autenticado"
                        ));
            }

            // Manejar diferentes tipos de Principal
            String username;
            Collection<? extends GrantedAuthority> authorities;

            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
                authorities = userDetails.getAuthorities();
            } else {
                username = authentication.getName();
                authorities = authentication.getAuthorities();
            }

            Optional<UserEntity> userOpt = service.obtenerUsuario(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Usuario no encontrado"
                        ));
            }

            UserEntity user = userOpt.get();

            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user", Map.of(
                            "id", user.getId(),
                            "nombre", user.getNombre(),
                            "apellido", user.getApellido(),
                            "correo", user.getCorreo(),
                            "rol", user.getTipoUsuario().getNombreTipo(),
                            "fechaRegistro", user.getFechaRegistro(),
                            "authorities", authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                    )
            ));

        } catch (Exception e) {
            //log.error("Error en /me endpoint: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "authenticated", false,
                            "message", "Error obteniendo datos de usuario"
                    ));
        }
    }
}
