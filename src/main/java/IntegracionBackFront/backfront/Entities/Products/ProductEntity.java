package IntegracionBackFront.backfront.Entities.Products;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter @Setter
@ToString @EqualsAndHashCode
@Table(name = "PRODUCTOS")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_Productos")
    @SequenceGenerator(sequenceName = "seq_Productos", name = "seq_Productos", allocationSize = 1)
    @Column(name = "IDPRODUCTO")
    private Long id;
    @Column(name = "NOMBREPRODUCTO")
    private String nombre;
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Column(name = "PRECIO")
    private double precio;
    @Column(name = "STOCK")
    private int stock;
    @Column(name = "FECHAINGRESO")
    private LocalDate fechaIngreso;
    @Column(name = "IDCATEGORIA")
    private Long categoriaId;
    @Column(name = "IDUSUARIOREGISTRO")
    private int usuarioId; //Usuario que lo registro
    @Column(name = "IMAGEN_URL")
    private String imagen_url;
}
