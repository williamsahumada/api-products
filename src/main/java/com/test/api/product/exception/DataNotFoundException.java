package com.test.api.product.exception;

import lombok.Getter;

public class DataNotFoundException extends Exception {

    @Getter
    String id;

    public DataNotFoundException(String id, String errorMessage) {
        super(errorMessage);
        this.id = id;
    }

}
