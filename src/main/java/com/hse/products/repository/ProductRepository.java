package com.hse.products.repository;

import com.hse.products.entity.Product;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    boolean existsProductsByCategoryId(Long categoryId);

    List<Product> findProductsByCategoryId(Long categoryId);


}
