package IntegracionBackFront.backfront.Repositories.Products;

import IntegracionBackFront.backfront.Entities.Products.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
