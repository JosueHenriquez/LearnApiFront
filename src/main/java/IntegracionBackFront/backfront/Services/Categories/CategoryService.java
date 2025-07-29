package IntegracionBackFront.backfront.Services.Categories;

import IntegracionBackFront.backfront.Entities.Categories.CategoryEntity;
import IntegracionBackFront.backfront.Exceptions.Category.ExceptionCategoryNotFound;
import IntegracionBackFront.backfront.Models.DTO.Categories.CategoryDTO;
import IntegracionBackFront.backfront.Repositories.Categories.CategoryRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repo;

    public Page<CategoryDTO> getAllCategories(int page, int size) {
        //Crear las páginas con los valroes de los parametros
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryEntity> pageEntity = repo.findAll(pageable);
        return pageEntity.map(this::convertirADTO);
    }

    public CategoryDTO insert(@Valid CategoryDTO jsonData) {
        if (jsonData == null){
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }
        try {
            CategoryEntity categoryEntity = convertirAEntity(jsonData);
            CategoryEntity categorySave = repo.save(categoryEntity);
            return convertirADTO(categorySave);
        }catch (Exception e){
            log.error("Error al registrar categoría: " + e.getMessage());
            throw new ExceptionCategoryNotFound("Error al registrar la categoría: " + e.getMessage());
        }
    }

    public CategoryDTO update(Long id, @Valid CategoryDTO jsonDTO) {
        if (jsonDTO == null){
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }
        //1. Verificar la existencia del usuario
        CategoryEntity categoryData = repo.findById(id).orElseThrow(()-> new ExceptionCategoryNotFound("Categoría no encontrada"));
        //2. Actualizar campos
        categoryData.setNombreCategoria(jsonDTO.getNombreCategoria());
        categoryData.setDescripcion(jsonDTO.getDescripcion());
        //3. Actualizar registro
        CategoryEntity categoryUpdate = repo.save(categoryData);
        //4. Retonar y convertir a DTO
        return convertirADTO(categoryUpdate);
    }

    public boolean delete(Long id) {
        try{
            //1. Verificar existencia de la categoría
            CategoryEntity objEntity = repo.findById(id).orElse(null);
            //2. Si el objeto existe se procede a eliminar
            if (objEntity != null){
                repo.deleteById(id);
                return true;
            }
            return false;
        }catch (EmptyResultDataAccessException e){
            throw new EmptyResultDataAccessException("No se encontró categoría con ID: "+id,1);
        }
    }

    private CategoryDTO convertirADTO(CategoryEntity objEntity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setIdCategoria(objEntity.getIdCategoria());
        dto.setNombreCategoria(objEntity.getNombreCategoria());
        dto.setDescripcion(objEntity.getDescripcion());
        dto.setFechaCreacion(objEntity.getFechaCreacion());
        return dto;
    }

    private CategoryEntity convertirAEntity(@Valid CategoryDTO json) {
        CategoryEntity objEntity = new CategoryEntity();
        objEntity.setDescripcion(json.getDescripcion());
        objEntity.setNombreCategoria(json.getNombreCategoria());
        objEntity.setFechaCreacion(json.getFechaCreacion());
        return objEntity;
    }
}
