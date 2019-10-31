package com.test.api.product.controller;

import com.test.api.product.dto.ProductDTO;
import com.test.api.product.exception.BusinessException;
import com.test.api.product.exception.DataNotFoundException;
import com.test.api.product.model.UploadFile;
import com.test.api.product.service.product.UploadProductFileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.*;
import static com.test.api.product.util.TesteUtil.*;
import static com.test.api.product.mock.ProductDTOMock.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductFileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UploadProductFileService uploadFileService;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        //mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) uploadFileService).build();
    }
    @Test
    public void showProducts_returnListOfProduct() throws Exception {
        given(uploadFileService.list()).willReturn(getProductsMock());
        mockMvc.perform(get("/teste/api/products/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        final List<ProductDTO> products = uploadFileService.list();
        assertEquals( 2, products.size() );
    }

    @Test
    public void givenFileName_whenFindFile_thenReturnSatus() throws Exception {
        String FILE_NAME = "teste.xls";
        given(uploadFileService.getFileStatus(FILE_NAME)).willReturn(new UploadFile(FILE_NAME, "Success"));
        mockMvc.perform(get("/teste/api/products/filestatus/" + FILE_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound()) ;
    }

    @Test
    public void givenAInvalidTargetOrSourceOrFile_whenUploadFile_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/teste/api/products/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .param("fileName","teste.xsl"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenTargetAndFileName_whenUploadFile_thenReturnOk() throws Exception {
        given(uploadFileService.uploadFile(anyString(), anyString(), anyString())).willReturn(true);
        mockMvc.perform(post("/teste/api/products/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .param("urlTarget","//URL")
                .param("fileName","teste.xsl"))
                .andExpect(status().isOk())
        .andExpect( jsonPath("$.status", is(HttpStatus.OK.value())));
    }

    @Test
    public void givenFileName_whenExecuteFileAndContainErrors_thenReturnInternalServerError() throws Exception {
        String FILE_NAME = "teste";
        given(uploadFileService.executeFile(FILE_NAME)).willReturn(false);
        mockMvc.perform(post("/teste/api/products/execute/" + FILE_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        .andExpect( jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }

    @Test
    public void givenFileName_whenExecuteFile_thenReturnListOfProducts() throws Exception {
        String FILE_NAME = "teste";
        given(uploadFileService.executeFile(FILE_NAME)).willReturn(true);
        given(uploadFileService.list()).willReturn(getProductsMock());
        mockMvc.perform(post("/teste/api/products/execute/" + FILE_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals( 2, uploadFileService.list().size() );
    }

    @Test
    public void givenIdProduct_whenDeleteProduct_thenReturnMessageResult() throws Exception {
        Long PRODUCT_ID = 1L;
        given(uploadFileService.entity(anyLong())).willReturn(getProductMock());
        given(uploadFileService.delete(getProductMock())).willReturn(true);
        mockMvc.perform(delete("/teste/api/products/delete/" + PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenIdProduct_whenDeleteProduct_thenDataNotFound() throws Exception {
        Long PRODUCT_ID = 1L;
        given(uploadFileService.entity(PRODUCT_ID)).willReturn(getProductMock());
        given(uploadFileService.delete(getProductMock())).willThrow(DataNotFoundException.class);
        mockMvc.perform(delete("/teste/api/products/delete/" + PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status", is(HttpStatus.NO_CONTENT.value())));
    }
    @Test
    public void givenFileName_whenThrowsBusinessException_thenReturnInternalServerError() throws Exception {
        String FILE_NAME = "teste";
        given(uploadFileService.executeFile(FILE_NAME)).willThrow(BusinessException.class);
        mockMvc.perform(post("/teste/api/products/execute/" + FILE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .param("urlTarget","//URL")
                .param("fileName","teste.xsl"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void givenProduct_whenUpdateProduct_thenReturnYourProductUpdated() throws Exception {
        given(uploadFileService.update(getProductMock())).willReturn(getProductMock());
        mockMvc.perform(put("/teste/api/products/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(toJson(getProductMock())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lm", is(getProductMock().getLm())))
                .andExpect(jsonPath("$.name", is(getProductMock().getName())))
                .andExpect(jsonPath("$.description", is(getProductMock().getDescription())))
                .andExpect(jsonPath("$.freeShipping", is(getProductMock().getFreeShipping())))
                .andExpect(jsonPath("$.category", is(getProductMock().getCategory())))
                .andExpect(jsonPath("$.price", is(getProductMock().getPrice())))
        ;
    }

    @Test
    public void givenANotFoundFileName_whenFindFileStatus_thenReturnNotFound() throws Exception {
        String FILE_NAME_NOT_FOUND = "x";
        given(uploadFileService.getFileStatus(null)).willReturn(null);
        mockMvc.perform(get("/teste/api/products/filestatus/" + FILE_NAME_NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenFileType_whenExecuteFiles_thenReturnSatusProcess() throws Exception {
        given(uploadFileService.executeFiles()).willReturn(true);
        given(uploadFileService.list()).willReturn(getProductsMock());
        mockMvc.perform(post("/teste/api/products/executefiles/xls")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenInvalidFileType_whenExecuteFiles_thenReturnBadRequest() throws Exception {
        String INVALID_FILE_TYPE = "x";

        mockMvc.perform(post("/teste/api/products/executefiles/" + INVALID_FILE_TYPE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenInvalidFileType_whenNotExecuteFiles_thenReturnInternalServerError() throws Exception {
        String INVALID_FILE_TYPE = "xls";
        given(uploadFileService.executeFiles()).willReturn(false);
        mockMvc.perform(post("/teste/api/products/executefiles/" + INVALID_FILE_TYPE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void givenInvalidFileType_whenContainErrorsForBusiness_thenReturnBadRequest() throws Exception {
        String INVALID_FILE_TYPE = "xls";
        given(uploadFileService.executeFiles()).willThrow(BusinessException.class);
        mockMvc.perform(post("/teste/api/products/executefiles/" + INVALID_FILE_TYPE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
