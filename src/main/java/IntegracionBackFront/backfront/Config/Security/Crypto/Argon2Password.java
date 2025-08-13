package IntegracionBackFront.backfront.Config.Security.Crypto;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;

/**
 * 	Agregar dependencia para la utilizacion de Argon2id
 * 	<dependency>
 * 		<groupId>de.mkammerer</groupId>
 * 		<artifactId>argon2-jvm</artifactId>
 * 		<version>2.5</version>
 * 	</dependency>
 */
@Service
public class Argon2Password {
    //Configuracion recomendada para Argon2id
    private static final int ITERATIONS = 10;
    private static final int MEMORY = 32768;
    private static final int PARALLELISM = 2;

    //Crear instancia de Argon2id
    private Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * Recibe el parametro password y encripta utilizando los valores definidos en la clase
     * @param password sin encriptar
     * @return, retorna una cadena basada en Argon2id
     */
    public String EncryptPassword(String password){
        return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password);
    }

    /**
     * El metodo recibe ambas contrasenas y mediante el metodo verify de Argon2, evalua si la contrasena es correcta.
     * @param passwordDB password proveniente de la base de datos
     * @param password password sin encriptacion, es el valor que el usuario ingresa en el login
     * @return
     */
    public boolean VerifyPassword(String passwordDB, String password){
        return argon2.verify(passwordDB, password);
    }

}
