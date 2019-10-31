package com.test.api.product.model;

import lombok.Builder;
import lombok.Data;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@Entity
public class Product {
    @Id
    Long lm;

    @NotEmpty(message = "Name é obrigatório!")
    String name;

    @NotEmpty(message = "FreeShipping é obrigatório!")
    String freeShipping;

    String description;

    @NotEmpty(message = "Preço é obrigatório!")
    String price;

    @NotEmpty(message = "Categoria é obrigatória!")
    String category;

    public Product() {}

    @Inject
    public Product(Long lm, String name, String freeShipping, String description, String price, String category) {
        this.lm = lm;
        this.name = name;
        this.freeShipping = freeShipping;
        this.description = description;
        this.price = price;
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
