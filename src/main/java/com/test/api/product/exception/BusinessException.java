package com.test.api.product.exception;

import lombok.Getter;

@Getter
public class BusinessException extends Exception implements MessageDetail {

    private final Integer code;
    private final transient Object[] args;

    public BusinessException(String customErrorMessage) {
        super(customErrorMessage);
        this.code = 409;
        this.args = null;
    }
}
