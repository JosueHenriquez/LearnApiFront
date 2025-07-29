package IntegracionBackFront.backfront.Repositories.Categories;

import IntegracionBackFront.backfront.Entities.Categories.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findAll(Pageable pageable);
}
