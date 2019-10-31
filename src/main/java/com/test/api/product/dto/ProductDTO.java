package com.test.api.product.dto;

import br.com.tecsinapse.exporter.annotation.TableCellMapping;
import lombok.Builder;
import lombok.Data;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class ProductDTO {
    String lm;

    @NotEmpty(message = "Name é obrigatório!")
    String name;

    @NotEmpty(message = "FreeShipping é obrigatório!")
    String freeShipping;

    String description;

    @NotEmpty(message = "Preço é obrigatório!")
    String price;

    @NotEmpty(message = "Categoria é obrigatória!")
    String category;

    public ProductDTO() {}

    @Inject
    public ProductDTO(String lm, String name, String freeShipping, String description, String price, String category) {
        this.lm = lm;
        this.name = name;
        this.freeShipping = freeShipping;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    @TableCellMapping(columnIndex = 0)
    public void setLm(String lm) {
        this.lm = lm;
    }

    @TableCellMapping(columnIndex = 1)
    public void setName(String name) {
        this.name = name;
    }

    @TableCellMapping(columnIndex = 2)
    public void setFreeShipping(String freeShipping) {
        this.freeShipping = freeShipping;
    }

    @TableCellMapping(columnIndex = 3)
    public void setDescription(String description) {
        this.description = description;
    }

    @TableCellMapping(columnIndex = 4)
    public void setPrice(String price) {
        this.price = price;
    }

    @TableCellMapping(columnIndex = 1)
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "lm=" + lm +
                ", name='" + name + '\'' +
                ", freeShipping=" + freeShipping +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}