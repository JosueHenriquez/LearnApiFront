package IntegracionBackFront.backfront.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    /**
     * Metodo proporcionado por Firebase
     * unicamente se debera crear el metodo para colocar el codigo.
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {

        String privateKey = System.getenv("FIREBASE_PRIVATE_KEY");

        String firebaseConfig = String.format(
                "{\"type\":\"%s\",\"project_id\":\"%s\",\"private_key_id\":\"%s\",\"private_key\":\"%s\"," +
                        "\"client_email\":\"%s\",\"client_id\":\"%s\",\"auth_uri\":\"%s\",\"token_uri\":\"%s\"," +
                        "\"auth_provider_x509_cert_url\":\"%s\",\"client_x509_cert_url\":\"%s\"}",
                System.getenv("FIREBASE_TYPE"),
                System.getenv("project_id"),
                System.getenv("private_key_id"),
                privateKey.replace("\n", "\\n"),
                System.getenv("client_email"),
                System.getenv("client_id"),
                System.getenv("auth_uri"),
                System.getenv("token_uri"),
                System.getenv("auth_provider_x509_cert_url"),
                System.getenv("client_x509_cert_url")
        );

        // 1. Cargar archivo JSON (sin src/main/resources)
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(
                "uploadspringimages-firebase-adminsdk-fbsvc-3af60a4524.json"
        );

        if (serviceAccount == null) {
            throw new FileNotFoundException("¡Archivo JSON no encontrado en resources!");
        }

        // 2. Configurar Firebase con bucket correcto
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                //.setStorageBucket("spring_uploadimages")  // Formato correcto
                .setStorageBucket("uploadspringimages.firebasestorage.app")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
