package IntegracionBackFront.backfront;

import IntegracionBackFront.backfront.Utils.Envars;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "IntegracionBackFront.backfront")
public class BackfrontApplication {

    public static void main(String[] args) {
        loadEnvironmentVariables();
        SpringApplication.run(BackfrontApplication.class, args);
    }

    static void loadEnvironmentVariables() {
        // Verificar si estamos en Heroku (PORT es una variable que siempre existe en Heroku)
        boolean isHeroku = System.getenv("PORT") != null;

        if (!isHeroku) {
            // Solo cargar .env en entorno local/desarrollo
            try {
                Dotenv dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();

                dotenv.entries().forEach(entry ->
                        System.setProperty(entry.getKey(), entry.getValue())
                );

                System.out.println("Variables .env cargadas localmente");
            } catch (Exception e) {
                System.out.println("No se pudo cargar archivo .env (posiblemente en producción)");
            }
        } else {
            System.out.println("Ejecutando en Heroku - usando variables de entorno del sistema");
        }

        // Asegurar que el puerto de Heroku tenga prioridad
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            System.setProperty("server.port", herokuPort);
        }
    }
}
