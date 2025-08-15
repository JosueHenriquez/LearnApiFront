package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

import java.util.Map;
import java.util.UUID;

// Indica que esta clase es un servicio de Spring y será gestionada por el contenedor de Spring
@Service
public class CloudinaryService {
    // Constante que define el tamaño máximo permitido para los archivos (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Extensiones de archivo permitidas para subir a Cloudinary
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    // Cliente de Cloudinary inyectado como dependencia
    private final Cloudinary cloudinary;

    // Constructor para la inyección de dependencias
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Sube una imagen a Cloudinary sin especificar carpeta
     * @param file Archivo de imagen a subir
     * @return URL segura (HTTPS) de la imagen subida
     * @throws IOException Si ocurre un error durante la subida
     * @throws IllegalArgumentException Si el archivo no pasa las validaciones
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // Primero valida el archivo
        validateImage(file);

        // Sube el archivo a Cloudinary con configuración básica:
        // - Tipo de recurso auto-detectado
        // - Calidad automática con nivel "good"
        Map<?, ?> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "auto",
                        "quality", "auto:good"
                ));

        // Retorna la URL segura de la imagen subida
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Sube una imagen a una carpeta específica en Cloudinary
     * @param file Archivo de imagen a subir
     * @param folder Carpeta de destino en Cloudinary
     * @return URL segura (HTTPS) de la imagen subida
     * @throws IOException Si ocurre un error durante la subida
     * @throws IllegalArgumentException Si el archivo no pasa las validaciones
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        // Valida el archivo antes de procesarlo
        validateImage(file);

        // Genera un nombre único para el archivo:
        // - Conserva la extensión original
        // - Agrega un prefijo y un UUID para evitar colisiones
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = "img_" + UUID.randomUUID() + fileExtension;

        // Configuración avanzada para la subida:
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,               // Carpeta de destino
                "public_id", uniqueFilename,    // Nombre único para el archivo
                "use_filename", false,          // No usar el nombre original
                "unique_filename", false,       // No generar nombre único (ya lo hicimos)
                "overwrite", false,             // No sobrescribir archivos existentes
                "resource_type", "auto",        // Auto-detectar tipo de recurso
                "quality", "auto:good"         // Optimización de calidad automática
        );

        // Realiza la subida del archivo
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        // Retorna la URL segura de la imagen
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Elimina una imagen de Cloudinary
     * @param publicId Identificador único de la imagen en Cloudinary
     * @throws IOException Si ocurre un error durante la eliminación
     *                    o si Cloudinary no retorna "ok" en la respuesta
     */
    public void deleteImage(String publicId) throws IOException {
        // Intenta eliminar la imagen, forzando invalidación de caché CDN
        Map<?, ?> result = cloudinary.uploader().destroy(publicId,
                ObjectUtils.asMap("invalidate", true));

        // Verifica que la operación fue exitosa
        if (!"ok".equals(result.get("result"))) {
            throw new IOException("No se pudo eliminar la imagen: " + publicId);
        }
    }

    /**
     * Valida el archivo antes de subirlo a Cloudinary
     * @param file Archivo MultipartFile a validar
     * @throws IllegalArgumentException Si el archivo no cumple con los requisitos:
     *         - Archivo vacío
     *         - Tamaño excede el límite
     *         - Nombre inválido
     *         - Extensión no permitida
     *         - Tipo MIME no es imagen
     */
    private void validateImage(MultipartFile file) {
        // Verifica si el archivo está vacío
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        // Verifica si el tamaño excede el límite permitido
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El tamaño máximo permitido es 5MB");
        }

        // Obtiene y valida el nombre original del archivo
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        // Extrae y valida la extensión del archivo
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)) {
            throw new IllegalArgumentException("Solo se permiten archivos JPG, JPEG, PNG");
        }

        // Verifica que el tipo MIME sea una imagen
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida");
        }
    }

}