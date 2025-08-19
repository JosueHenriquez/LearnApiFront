package IntegracionBackFront.backfront.Controller.Cloudinary;

import IntegracionBackFront.backfront.Services.Cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

// Indica que esta clase es un controlador REST y manejará solicitudes HTTP
@RestController
// Define la ruta base para todos los endpoints en este controlador: "/api/image"
@RequestMapping("/api/image")
@CrossOrigin
public class ImageController {

    // Inyección del servicio Cloudinary (maneja la lógica de subida de imágenes)
    @Autowired
    private final CloudinaryService cloudinaryService;

    // Constructor con inyección de dependencias (recomendado sobre @Autowired)
    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // Endpoint POST para subir una imagen a Cloudinary (sin carpeta específica)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            // Llama al servicio para subir la imagen y obtener su URL
            String imageUrl = cloudinaryService.uploadImage(file);

            // Retorna una respuesta exitosa (200 OK) con un JSON estructurado
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida exitosamente",
                    "url", imageUrl
            ));
        } catch (IOException e) {
            // Si hay un error, retorna 500 Internal Server Error con un mensaje
            return ResponseEntity.internalServerError().body("Error al subir la imagen");
        }
    }

    // Endpoint POST para subir una imagen a una carpeta específica en Cloudinary
    @PostMapping("/upload-to-folder")
    public ResponseEntity<?> uploadImageToFolder(
            @RequestParam("image") MultipartFile file,
            @RequestParam String folder) {  // Parámetro adicional para la carpeta
        try {
            // Sube la imagen a la carpeta especificada
            String imageUrl = cloudinaryService.uploadImage(file, folder);

            // Retorna la respuesta exitosa con la URL de la imagen
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida exitosamente",
                    "url", imageUrl
            ));
        } catch (IOException e) {
            // Manejo de errores
            return ResponseEntity.internalServerError().body("Error al subir la imagen");
        }
    }
}
