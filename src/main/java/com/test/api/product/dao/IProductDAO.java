package com.test.api.product.dao;

import com.test.api.product.dto.ProductDTO;
import com.test.api.product.service.CRUD;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductDAO extends CRUD<ProductDTO> {

}
