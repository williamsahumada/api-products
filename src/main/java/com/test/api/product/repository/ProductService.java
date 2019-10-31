package com.test.api.product.repository;

import com.test.api.product.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ProductService extends CrudRepository<Product, Long> {
}
