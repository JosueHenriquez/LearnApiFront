package IntegracionBackFront.backfront.Config.Cloudinary;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// Indica que esta clase es una configuración de Spring y se procesará al iniciar la aplicación
@Configuration
public class CloudinaryConfig {

    // Variables para almacenar las credenciales de Cloudinary (no se usan directamente aquí)
    private String cloudName;
    private String apiKey;
    private String apiSecret;

    // Crea un Bean de tipo Cloudinary que será inyectable en otros componentes de Spring
    @Bean
    public Cloudinary cloudinary() {
        // Carga las variables de entorno desde un archivo .env usando la librería Dotenv
        Dotenv dotenv = Dotenv.load();

        // Crea un Map para almacenar la configuración necesaria de Cloudinary
        Map<String, String> config = new HashMap<>();

        // Obtiene las credenciales desde las variables de entorno y las guarda en el Map
        config.put("cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"));  // Nombre de la nube en Cloudinary
        config.put("api_key", dotenv.get("CLOUDINARY_API_KEY"));        // API Key para autenticación
        config.put("api_secret", dotenv.get("CLOUDINARY_API_SECRET"));  // API Secret (clave secreta)

        // Retorna una nueva instancia de Cloudinary con la configuración cargada
        return new Cloudinary(config);
    }
}
