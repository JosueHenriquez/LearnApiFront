package IntegracionBackFront.backfront.Services.Products;

import IntegracionBackFront.backfront.Entities.Products.ProductEntity;
import IntegracionBackFront.backfront.Exceptions.Products.ExceptionProductDontRegister;
import IntegracionBackFront.backfront.Exceptions.Products.ExceptionsNotFound;
import IntegracionBackFront.backfront.Models.DTO.Products.ProductDTO;
import IntegracionBackFront.backfront.Repositories.Products.ProductRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public Page<ProductDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> pageEntity = repo.findAll(pageable);
        return pageEntity.map(this::ConvertirADTO);
    }

    public ProductDTO insert(@Valid ProductDTO json) {
        if (json == null){
            throw new IllegalArgumentException("La información del producto no puede ser nulo");
        }
        try{
            ProductEntity objData = ConvertirAEntity(json);
            ProductEntity productoGuardado = repo.save(objData);
            return ConvertirADTO(productoGuardado);
        }catch (Exception e){
            log.error("Error al registrar el producto" + e.getMessage());
            throw new ExceptionProductDontRegister("El producto no pudo ser registrado");
        }
    }

    public ProductDTO update(Long id, @Valid ProductDTO json) {
        //1. Verificar existencia
        ProductEntity productoExistente = repo.findById(id).orElseThrow(()-> new ExceptionsNotFound("Producto no encontrado."));
        //2. Actualizar campos
        productoExistente.setNombre(json.getNombre());
        productoExistente.setDescripcion(json.getDescripcion());
        productoExistente.setPrecio(json.getPrecio());
        productoExistente.setStock(json.getStock());
        productoExistente.setFechaIngreso(json.getFechaIngreso());
        productoExistente.setCategoriaId(json.getCategoriaId());
        productoExistente.setUsuarioId(json.getUsuarioId());
        productoExistente.setImagen_url(json.getImagen_url());
        //3. Actualización del registro
        ProductEntity productoActualizado = repo.save(productoExistente);
        //4. Convertir a DTO
        return ConvertirADTO(productoActualizado);
    }

    public boolean delete(Long id) {
        //1. Verificar la existencia del producto
        ProductEntity existencia = repo.findById(id).orElse(null);
        //2. Si el valor existe lo elimina
        if (existencia != null){
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductDTO ConvertirADTO(ProductEntity objEntity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(objEntity.getId());
        dto.setNombre(objEntity.getNombre());
        dto.setDescripcion(objEntity.getDescripcion());
        dto.setPrecio(objEntity.getPrecio());
        dto.setStock(objEntity.getStock());
        dto.setFechaIngreso(objEntity.getFechaIngreso());
        dto.setCategoriaId(objEntity.getCategoriaId());
        dto.setUsuarioId(objEntity.getUsuarioId());
        dto.setImagen_url(objEntity.getImagen_url());
        return dto;
    }

    private ProductEntity ConvertirAEntity(@Valid ProductDTO json) {
        ProductEntity entity = new ProductEntity();
        entity.setNombre(json.getNombre());
        entity.setDescripcion(json.getDescripcion());
        entity.setPrecio(json.getPrecio());
        entity.setStock(json.getStock());
        entity.setFechaIngreso(json.getFechaIngreso());
        entity.setCategoriaId(json.getCategoriaId());
        entity.setUsuarioId(json.getUsuarioId());
        entity.setImagen_url(json.getImagen_url());
        return entity;
    }
}
