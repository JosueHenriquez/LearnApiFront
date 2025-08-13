package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

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
        Map options = ObjectUtils.asMap(
                "folder", folder,
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true,
                "resource_type", "auto"
        );
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
