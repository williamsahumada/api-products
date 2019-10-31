package com.test.api.product.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiResponseDTO {
    private String message;
    private int status;
    private String error;

    public ApiResponseDTO(String message, int status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }
}
