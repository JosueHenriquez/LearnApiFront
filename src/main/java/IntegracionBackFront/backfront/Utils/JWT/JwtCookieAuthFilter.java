package IntegracionBackFront.backfront.Utils.JWT;

import IntegracionBackFront.backfront.Config.SecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Filtro se ejecuta una vez por cada solicitud HTTP
 * Componente gestionado por Spring
 */
@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    //Logger para registrar eventos y errores
    private static final Logger log = LoggerFactory.getLogger(JwtCookieAuthFilter.class);
    //Nombre de la cookie que contiene el token JWT
    private static final String AUTH_COOKIE_NAME = "authToken";
    //Utilidad para trabajar con tokens JWT
    private final JWTUtils jwtUtils;

    //Constructor que recibe la dependencia JWTUtils (inyección de dependecias)
    @Autowired
    public JwtCookieAuthFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    //Metodo principal que se ejecuta en cada solicitud
    //Debes ocupar HttpServletRequest, HttpServletResponse y FilterChain de la dependecia jakarta
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //Excluir endpoint púlicos que no necesitan autenticación
        if (request.getRequestURI().equals("/api/auth/login") || request.getRequestURI().equals("/api/auth/logout")){
            // Si es un endpoint público, continuar con la cadena de filtros sin autenticación
            filterChain.doFilter(request, response);
            return;
        }

        //Intentar procesar la autenticación JWT
        try{
            //Extraer token JWT de las cookies de la solicitud
            String token = extractTokenFromCookies(request);

            //Verificar si el token existe y no está vacío
            if (token == null || token.isBlank()){
                //Si no hay token, enviar error de no autorizado
                sendError(response, "Token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //Parsear y validar el token usando JWTUtils
            Claims claims = jwtUtils.parseToken(token);

            // Crear objeto de autenticación con la información del token
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), // Nombre de usuario (subject del token)
                            null, // Credenciales (no necesarias después de autenticado)
                            Arrays.asList(() -> "ROLE_USER") // Roles básicos, ajusta según necesites
                    );

            // Establecer la autenticación en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e) {
            // El token ha expirado
            log.warn("Token expirado: {}", e.getMessage());
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            // El token tiene un formato incorrecto
            log.warn("Token malformado: {}", e.getMessage());
            sendError(response, "Token inválido", HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            // Cualquier otro error inesperado
            log.error("Error de autenticación", e);
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extrae el token JWT de las cookies de la solicitud
     * @param request
     * @return
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        //Si no hay cookies, retorna null
        if (cookies == null) return null;

        //Buscar la cookie con el nombre AUTH_COOKIE_NAME y obtener su valor
        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException{
        response.setContentType("application/json");    // Establece el tipo de contenido
        response.setStatus(status);                     // Establece código de estado HTTP
        response.getWriter().write(String.format(
            "{\"error\": \"%s\", \"status\": %d}", message, status)); // Escribir respuesta JSON
    }

    private boolean isPublicEndpoint(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login");
    }

    // Obtiene los permisos/roles del usuario desde el token (pendiente de implementación)
    private Collection<? extends GrantedAuthority> getAuthorities(String token) {
        // Implementa según tus necesidades
        // Puedes extraer roles/permisos del token si los incluyes
        return Collections.emptyList();
    }
}
