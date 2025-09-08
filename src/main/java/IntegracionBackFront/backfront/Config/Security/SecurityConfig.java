package IntegracionBackFront.backfront.Config.Security;

import IntegracionBackFront.backfront.Utils.JWT.JwtCookieAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtCookieAuthFilter jwtCookieAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource; // Inyecta CorsConfigurationSource

    public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // ← Configura CORS aquí
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ← Permite preflight requests
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers("api/auth/me").authenticated()

                        //ENDPOINTS CATEGORIA
                        .requestMatchers(HttpMethod.GET, "/api/category/getDataCategory-paginado").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/category/getDataCategories").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/category/newCategory").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/category/updateCategory/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/category/deleteCategory/{id}").authenticated()

                        //ENDPOINT PRODUCTOS
                        .requestMatchers(HttpMethod.GET, "/api/products/getAllProducts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/newProduct").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/products/updateProduct/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/deleteProduct/{id}").authenticated()

                        //ENDPOINT CON ROLES ESPECIFICOS
                        .requestMatchers("/api/test/admin-only").hasAnyAuthority("Administrador")
                        .requestMatchers("/api/test/cliente-only").hasAnyAuthority("Cliente")
                        .requestMatchers("/api/test/admin-cliente-only").hasAnyAuthority("Cliente", "Administrador")
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}