package IntegracionBackFront.backfront;

import IntegracionBackFront.backfront.Utils.Envars;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackfrontApplication{

	public static void main(String[] args) {
		loadEnvVariables();
		//Esta linea no se borra
		SpringApplication.run(BackfrontApplication.class, args);
	}

	static void loadEnvVariables(){
		//Codigo para cargar los valores del archivo .env sobre el archivo application.properties
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		//Carga de todas las variables en application.properties
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
	}
}
