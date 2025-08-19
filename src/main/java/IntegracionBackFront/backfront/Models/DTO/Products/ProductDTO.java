package IntegracionBackFront.backfront.Models.DTO.Products;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductDTO {

    private Long id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String descripcion;

    @Positive(message = "El precio debe ser mayor a cero.")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos entero y 2 decimales")
    private double precio;

    @Positive(message = "El stock del producto debe ser positivo")
    private int stock;

    @PastOrPresent(message = "La fecha de ingreso debe ser en el pasado o presente")
    private LocalDate fechaIngreso;
    private Long categoriaId;
    private int usuarioId; //Usuario que lo registro
    private String imagen_url;
}
