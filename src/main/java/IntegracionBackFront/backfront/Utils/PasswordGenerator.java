package IntegracionBackFront.backfront.Utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    public static String generateSecurePassword(int length) {
        if (length < 12) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 12 caracteres");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        List<String> charCategories = new ArrayList<>(List.of(
                LOWERCASE, UPPERCASE, NUMBERS, SPECIALS));

        // Aseguramos al menos un carácter de cada categoría
        for (String category : charCategories) {
            password.append(category.charAt(random.nextInt(category.length())));
        }

        // Completamos el resto de la longitud
        for (int i = password.length(); i < length; i++) {
            String category = charCategories.get(random.nextInt(charCategories.size()));
            password.append(category.charAt(random.nextInt(category.length())));
        }

        // Mezclamos los caracteres para mayor aleatoriedad
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars, random);

        StringBuilder finalPassword = new StringBuilder();
        passwordChars.forEach(finalPassword::append);

        return finalPassword.toString();
    }
}
