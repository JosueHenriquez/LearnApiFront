package IntegracionBackFront.backfront.Utils.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

//Anotación que marca esta clase como un componente de Spring
@Component
public class JWTUtils {

    @Value("${security.jwt.secret}")
    private String jwtSecreto;                  // 32 caracteres por seguridad
    @Value("${security.jwt.issuer}")
    private String issuer;                      // Firma del token
    @Value("${security.jwt.expiration}")
    private long expiracionMs;                  // Tiempo de expiración

    private final Logger log = LoggerFactory.getLogger(JWTUtils.class);

    public String create(String id, String nombre){
        //Decodifica el secreto Base64 y crea una clave HMAC-SHA segura
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto));

        //Obtiene la fecha catual y calcula la fecha de expiración
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiracionMs);

        //Construye el token con sus componentes
        return Jwts.builder()
                .setId(id)                                              // ID único (JWT ID)
                .setIssuedAt(now)                                       // Fecha de emisión
                .setSubject(nombre)                                     // Sujeto (usuario)
                .setIssuer(issuer)                                      // Emisor del token
                .setExpiration(expiracionMs >= 0 ? expiration : null)   // Expiración (si es >= 0)
                .signWith(signingKey, SignatureAlgorithm.HS256)         // Firma con algoritmo HS256
                .compact();                                             // Convierte a String compacto
    }

    /**
     * Obtiene el subject (nombre) del JWT
     * @param jwt Token JWT como String
     * @return String con el subject del token
     */
    public String getValue(String jwt){
        //Parsea los claims y devuelve el subject
        Claims claims = parseClaims(jwt);
        return claims.getSubject();
    }

    public String getKey(String jwt){
        // Parsea los claims y devuelve el ID
        Claims claims = parseClaims(jwt);
        return claims.getId();
    }

    public Claims parseToken(String jwt) throws ExpiredJwtException, MalformedJwtException {
        return parseClaims(jwt);
    }

    public String extractTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if ("authToken".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean validate(String token){
        try{
            parseClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    //######################## METODOS COMPLEMENTARIOS ########################

    /**
     * Metodo privado para parsear los claims de un JWT
     * @param jwt Token a parsear
     * @return Claims del token
     */
    private Claims parseClaims(String jwt) {
        //Configura el parse con la clave de firma y parsea el token
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto)))  // Clave de firma
                .build()
                .parseClaimsJws(jwt)
                .getBody();

    }
}
