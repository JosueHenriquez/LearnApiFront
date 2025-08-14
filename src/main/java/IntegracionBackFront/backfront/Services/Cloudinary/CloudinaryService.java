package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        // 1. Generar nombre único conservando la extensión original
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = "img_" + UUID.randomUUID().toString() + fileExtension;

        // 2. Configuración de opciones optimizada
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "public_id", uniqueFilename,  // Usamos directamente el nombre único
                "use_filename", false,        // Desactivado ya que usamos public_id
                "unique_filename", false,     // No necesario con UUID
                "overwrite", false,           // Prevención adicional
                "resource_type", "auto",      // Auto-detectar tipo
                "quality", "auto:good"        // Optimización de calidad
        );

        // 3. Subir la imagen
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        // 4. Obtener URL segura
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
