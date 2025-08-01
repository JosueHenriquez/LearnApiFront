package IntegracionBackFront.backfront.Config;

import IntegracionBackFront.backfront.Utils.Envars;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void init() throws IOException {
        //Verificar si Firebase ya esta inicializado
        if (!FirebaseApp.getApps().isEmpty()){
            logger.info("Firebase ya esta inicializado");
            return;
        }
        try{
            // Opción 1: Usar variables de entorno (recomendado para producción)
            if(System.getenv("FIREBASE_PRIVATE_KEY") != null) {
                logger.info("Inicializando Firebase desde variables de entorno heroku");
                initFromEnvVars();
            }else{
                // Opción 2: Usar archivo JSON (solo para desarrollo)
                logger.info("Inicializando Firebase desde archivo local .json");
                initFromJsonFile();
            }
        }catch (IOException e){
            logger.error("Error inicializando Firebase." + e.getMessage());
            throw new RuntimeException("Error inicializando Firebase");
        }
    }

    private void initFromEnvVars() throws IOException {
        String privateKey = System.getenv("FIREBASE_PRIVATE_KEY")
                .replace("\\n", "\n"); // Corrección crucial aquí

        String firebaseConfig = String.format(
                "{\"type\":\"%s\",\"project_id\":\"%s\",\"private_key_id\":\"%s\"," +
                        "\"private_key\":\"%s\",\"client_email\":\"%s\",\"client_id\":\"%s\"," +
                        "\"auth_uri\":\"%s\",\"token_uri\":\"%s\"," +
                        "\"auth_provider_x509_cert_url\":\"%s\"," +
                        "\"client_x509_cert_url\":\"%s\"}",
                System.getenv("FIREBASE_TYPE"),
                System.getenv("FIREBASE_PROJECT_ID"),
                System.getenv("FIREBASE_PRIVATE_KEY_ID"),
                privateKey,
                System.getenv("FIREBASE_CLIENT_EMAIL"),
                System.getenv("FIREBASE_CLIENT_ID"),
                System.getenv("FIREBASE_AUTH_URI"),
                System.getenv("FIREBASE_TOKEN_URI"),
                System.getenv("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"),
                System.getenv("FIREBASE_CLIENT_X509_CERT_URL")
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ByteArrayInputStream(firebaseConfig.getBytes())))
                .setStorageBucket(System.getenv("FIREBASE_STORAGE_BUCKET"))
                .build();

        FirebaseApp.initializeApp(options);
    }

    private void initFromJsonFile() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream(Envars.file_json);

        if (serviceAccount == null) {
            throw new IOException("Archivo JSON no encontrado en resources");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(Envars.bucket)
                .build();

        FirebaseApp.initializeApp(options);
    }
}