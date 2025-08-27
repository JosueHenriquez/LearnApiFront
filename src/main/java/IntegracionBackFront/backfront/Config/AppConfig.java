package IntegracionBackFront.backfront.Config;

import IntegracionBackFront.backfront.Utils.JWT.JWTUtils;
import IntegracionBackFront.backfront.Utils.JWT.JwtCookieAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JwtCookieAuthFilter jwtCookieAuthFilter(JWTUtils jwtUtils){
        return new JwtCookieAuthFilter(jwtUtils);
    }
}
