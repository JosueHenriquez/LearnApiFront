package IntegracionBackFront.backfront.Controller.Cloudinary;

import IntegracionBackFront.backfront.Services.Cloudinary.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final CloudinaryService cloudinaryService;

    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida exitosamente",
                    "url", imageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al subir la imagen");
        }
    }

    @PostMapping("/upload-to-folder")
    public ResponseEntity<?> uploadImageToFolder(
            @RequestParam("image") MultipartFile file,
            @RequestParam String folder) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, folder);
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida exitosamente",
                    "url", imageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al subir la imagen");
        }
    }
}
