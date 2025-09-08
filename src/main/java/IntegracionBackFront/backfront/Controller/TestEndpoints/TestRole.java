package IntegracionBackFront.backfront.Controller.TestEndpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/test")
public class TestRole {

    @GetMapping("/cliente-only")
    @PreAuthorize("hasAuthority('ROLE_Cliente')")
    public ResponseEntity<?> clienteEndPoint(){
        return ResponseEntity.ok("Solo para clientes");
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ROLE_Administrador')")
    public ResponseEntity<?> adminEndPoint(){
        return ResponseEntity.ok("Solo para administradores");
    }

    @GetMapping("/almacenista-only")
    @PreAuthorize("hasAuthority('ROLE_Almacenista')")
    public ResponseEntity<?> AlmacenistaEndPoint(){
        return ResponseEntity.ok("Solo para almacenistas");
    }

    @GetMapping("/admin-cliente-only")
    @PreAuthorize("hasAnyAuthority('ROLE_Administrador', 'ROLE_Cliente')")
    public ResponseEntity<?> clienteAdminEndPoint(){
        return ResponseEntity.ok("Solo para administrador y clientes");
    }

    @GetMapping("/debug-auth")
    public ResponseEntity<?> debugAuth(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "name", authentication.getName(),
                "authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                "authenticated", authentication.isAuthenticated()
        ));
    }
}
