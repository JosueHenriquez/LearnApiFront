package IntegracionBackFront.backfront.Repositories.Categories;

import IntegracionBackFront.backfront.Entities.Categories.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
