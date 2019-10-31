package com.test.api.product.service;

import com.test.api.product.exception.BusinessException;
import com.test.api.product.model.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UploadFileService {

    boolean uploadFile(String urlTarget, String urlSource, String fileName) throws IOException;

    boolean uploadFile(MultipartFile file, String source) throws IOException;

    boolean executeFiles() throws BusinessException;

    boolean executeFile(String url) throws BusinessException;

    UploadFile getFileStatus(String fileName);

}