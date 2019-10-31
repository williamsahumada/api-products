package com.test.api.product.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@Entity
public class UploadFile {
    @Id
    @Column(name = "fileName")
    private String fileName;
    @Column(name = "fileStatus")
    private String fileStatus;

    public UploadFile(){}

    public UploadFile(String fileName, String fileStatus) {
        this.fileName = fileName;
        this.fileStatus = fileStatus;
    }
}
