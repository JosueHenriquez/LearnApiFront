package IntegracionBackFront.backfront.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        // Opción 1: Usar variables de entorno (recomendado para producción)
        if (System.getenv("FIREBASE_PRIVATE_KEY") != null) {
            initFromEnvVars();
        }
        // Opción 2: Usar archivo JSON (solo para desarrollo)
        else {
            initFromJsonFile();
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
                System.getenv("FIREBASE_PROJECT_ID"), // Nombre consistente
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
                .setStorageBucket(System.getenv("uploadspringimages.firebasestorage.app"))
                .build();

        FirebaseApp.initializeApp(options);
    }

    private void initFromJsonFile() throws IOException {
//        InputStream serviceAccount = getClass().getClassLoader()
//                .getResourceAsStream("uploadspringimages-firebase-adminsdk-fbsvc-3af60a4524.json");

        InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("firebase-config.json");

        if (serviceAccount == null) {
            throw new IOException("Archivo JSON no encontrado en resources");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("uploadspringimages.appspot.com") // Usa el formato correcto
                .build();

        FirebaseApp.initializeApp(options);
    }
}