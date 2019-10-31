package com.test.api.product.converter;

import com.test.api.product.dto.ProductDTO;
import com.test.api.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ConverterUtil {
    public static ProductDTO buildProductDTO(Product product){
        return ProductDTO.builder()
                .lm(String.valueOf(product.getLm()))
                .name(product.getName())
                .description(product.getDescription())
                .freeShipping(product.getFreeShipping())
                .price(product.getPrice())
                .category(product.getCategory()).build();
    }

    public static Product buildProductRepository(ProductDTO product){
        return Product.builder()
                .lm(Long.valueOf(product.getLm()))
                .name(product.getName())
                .description(product.getDescription())
                .freeShipping(product.getFreeShipping())
                .price(product.getPrice())
                .category(product.getCategory()).build();
    }
}
