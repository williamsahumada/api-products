package com.test.api.product.controller;

import com.test.api.product.dto.ApiResponseDTO;
import com.test.api.product.dto.ProductDTO;
import com.test.api.product.exception.BusinessException;
import com.test.api.product.exception.DataNotFoundException;
import com.test.api.product.model.UploadFile;
import com.test.api.product.service.EnumUploadFileType;
import com.test.api.product.service.product.UploadProductFileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.test.api.product.constants.Messages.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/teste/api/products")
public class ProductFileController {

    private UploadProductFileService uploadFileService;

    @Autowired
    public ProductFileController(UploadProductFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @ApiOperation(value = "Mostra a lista de produtos.")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Lista de produtos mostrada com sucesso!"))
    @GetMapping("/")
    public ResponseEntity listProducts(){
        return ResponseEntity.ok(uploadFileService.list());
    }

    @ApiOperation(value = "Mostra o estatus de um arquivo.")
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Status do arquivo mostrado com sucesso!"),
            @ApiResponse(code = 500, message = "Internal Server Error: Unexpected server error")
    })
    @GetMapping("/filestatus/{fileName}")
    public ResponseEntity getFileStatus(@PathVariable(value = "fileName") final String fileName){
        UploadFile uploadFile = uploadFileService.getFileStatus(fileName);
        if (uploadFile == null)
            return ResponseEntity.notFound().build();
        else
            return new ResponseEntity(uploadFile, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Carregar arquivos de produtos numa pasta temporaria.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Processo executado com sucesso!"),
            @ApiResponse(code = 500, message = "Internal Server Error: Unexpected server error"),
            @ApiResponse(code = 400, message = "Problema nos dados enviados!")
    })
    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam(value = "urlTarget", required = false) final String target,
                                     @RequestParam(value = "urlSource", required = false) final String source,
                                     @RequestParam(value = "fileName", required = false) final String fileName,
                                     @RequestParam(value = "file", required = false) final MultipartFile file) throws IOException {

        if ((StringUtils.isEmpty(target) && StringUtils.isEmpty(file)) ||
                (StringUtils.isEmpty(file) && ((!StringUtils.isEmpty(fileName) && StringUtils.isEmpty(target)) || (StringUtils.isEmpty(fileName) && !StringUtils.isEmpty(target)))))
            return new ResponseEntity<>(ERROR_EMPTY_PARAMETERS, HttpStatus.BAD_REQUEST);
        if(!StringUtils.isEmpty(file)){
            uploadFileService.uploadFile(file, source);
        } else {
            uploadFileService.uploadFile(target, source, fileName);
        }
        return ResponseEntity.ok(new ApiResponseDTO(PROCESS_EXECUTED, HttpStatus.OK.value(), null));

    }

    @ApiOperation(value = "Insere produtos via arquivo segundo os arquivos carregado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Produto inserido com sucesso!"),
            @ApiResponse(code = 500, message = "Internal Server Error: Unexpected server error"),
            @ApiResponse(code = 400, message = "Problema nos dados enviados!")
    })
    @PostMapping("/execute/{fileName}")
    public ResponseEntity executeFile(@PathVariable(value = "fileName") final String fileName) throws BusinessException {
        try {
            if (uploadFileService.executeFile(fileName))
                return new ResponseEntity<>(uploadFileService.list(), HttpStatus.OK);
            else
                return new ResponseEntity<>(new ApiResponseDTO(ERROR_PROCESS_EXECUTE, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Insere produtos desde o tipo (xls) de arquivo carregados na pasta temporal (varios).")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Produtos inserido com sucesso!"),
            @ApiResponse(code = 500, message = "Internal Server Error: Unexpected server error"),
            @ApiResponse(code = 400, message = "Problema nos dados enviados!")
    })
    @PostMapping("/executefiles/{fileType}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity executeFiles(@PathVariable(value = "fileType") final String fileType) {
        if (ObjectUtils.isEmpty(EnumUploadFileType.getByName(fileType.toLowerCase())))
            return new ResponseEntity<>(ERROR_INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
        try {
            if (uploadFileService.executeFiles())
                return new ResponseEntity<>(uploadFileService.list(), HttpStatus.OK);
            else
                return new ResponseEntity<>(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @ApiOperation(value = "Atualiza produtos via request body.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Produto atualizado com sucesso!"),
            @ApiResponse(code = 500, message = "Internal Server Error: Unexpected server error"),
            @ApiResponse(code = 400, message = "Problema nos dados enviados!")
    })
    @PutMapping("/update")
    public ResponseEntity update(@RequestBody @Valid final ProductDTO productRequest) throws DataNotFoundException{
        return new ResponseEntity<>(uploadFileService.update(productRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "Deleta produtos via arquivo.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Produto deletado com sucesso!"),
            @ApiResponse(code = 500, message = "Internal Server Error: Unexpected server error"),
            @ApiResponse(code = 204, message = "Produto não encontrado para deletar."),
    })
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity delete(@PathVariable(value = "productId") final Long productId) throws DataNotFoundException {
        String resultMessage = DELETED;
        HttpStatus httpStatus = HttpStatus.OK;
        String error = null;
        try {
            uploadFileService.delete(uploadFileService.entity(productId));
        } catch (DataNotFoundException e) {
            resultMessage = null;
            httpStatus = HttpStatus.NO_CONTENT;
            error = e.getMessage();
        }
        return ResponseEntity.status(httpStatus).body(new ApiResponseDTO(resultMessage, httpStatus.value(), error));
    }

}

