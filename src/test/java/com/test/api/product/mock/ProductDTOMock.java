package com.test.api.product.mock;

import com.test.api.product.dto.ProductDTO;

import java.util.Arrays;
import java.util.List;

public class ProductDTOMock {
    public static ProductDTO getProductMock() {
        return ProductDTO.builder().lm("1").name("Pro1").description("Teste 1").freeShipping("0").price("100").category("123123").build();
    }

    public static List<ProductDTO> getProductsMock(){
        return Arrays.asList(
                ProductDTO.builder().lm("1").name("Pro1").description("Teste 1").freeShipping("0").price("100").category("123123").build(),
                ProductDTO.builder().lm("2").name("Pro2").description("Teste 2").freeShipping("0").price("150").category("123123").build());
    }
}
