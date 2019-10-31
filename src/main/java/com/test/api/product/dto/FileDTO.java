package com.test.api.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDTO {
    String fileName;
    String fileStatus;

    public FileDTO(String fileName, String fileStatus) {
        this.fileName = fileName;
        this.fileStatus = fileStatus;
    }
}
