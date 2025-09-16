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
                        .requestMatchers("/api/auth/me").authenticated()

                        //ENDPOINTS CATEGORIA
                        .requestMatchers(HttpMethod.GET, "/api/category/getDataCategory-paginado").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/category/newCategory").hasAuthority("ROLE_Administrador")
                        .requestMatchers(HttpMethod.PUT, "/api/category/updateCategory/{id}").hasAuthority("ROLE_Administrador")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/deleteCategory/{id}").hasAuthority("ROLE_Administrador")

                        //ENDPOINT PRODUCTOS
                        .requestMatchers(HttpMethod.GET, "/api/products/getAllProducts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/products/newProduct").hasAnyAuthority("ROLE_Administrador", "ROLE_Almacenista")
                        .requestMatchers(HttpMethod.PUT, "/api/products/updateProduct/{id}").hasAnyAuthority("ROLE_Administrador", "ROLE_Almacenista")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/deleteProduct/{id}").hasAnyAuthority("ROLE_Administrador", "ROLE_Almacenista")
                        .requestMatchers(HttpMethod.POST, "/api/image/upload-to-folder").hasAnyAuthority("ROLE_Administrador", "ROLE_Almacenista")

                        //ENDPOINTS USUARIOS
                        .requestMatchers(HttpMethod.GET, "/api/users/getDataUsers").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/newUser").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/updateUser/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/deleteUser/{id}").authenticated()

                        //ENDPOINT CON ROLES ESPECIFICOS
                        .requestMatchers("/api/test/admin-only").hasAnyAuthority("ROLE_Administrador")
                        .requestMatchers("/api/test/cliente-only").hasAnyAuthority("ROLE_Cliente")
                        .requestMatchers("/api/test/almacenista-only").hasAnyAuthority("ROLE_Almacenista")
                        .requestMatchers("/api/test/admin-cliente-only").hasAnyAuthority("ROLE_Cliente", "ROLE_Administrador")
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