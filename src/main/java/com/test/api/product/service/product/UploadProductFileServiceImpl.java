package com.test.api.product.service.product;

import br.com.tecsinapse.exporter.importer.parser.SpreadsheetParser;
import com.test.api.product.constants.FileConstants;
import com.test.api.product.dto.ProductDTO;
import com.test.api.product.exception.BusinessException;
import com.test.api.product.exception.DataNotFoundException;
import com.test.api.product.model.Product;
import com.test.api.product.model.UploadFile;
import com.test.api.product.repository.ProductService;
import com.test.api.product.repository.UploadFileService;
import com.test.api.product.utils.ValidatorFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.test.api.product.converter.ConverterUtil.*;
import static com.test.api.product.constants.Messages.*;


@Service
public class UploadProductFileServiceImpl implements UploadProductFileService {
    private Logger LOGGER = LoggerFactory.getLogger(UploadProductFileServiceImpl.class.getName());
    private static String upload_folder = "";


    @Autowired
    ProductService productService;

    @Autowired
    UploadFileService uploadFileService;


    @Override
    public boolean uploadFile(String urlSource, String urlTarget, String fileName) throws IOException {
        return moveFilesToBeProcessed(urlSource, urlTarget, fileName);
    }

    @Override
    public boolean uploadFile(MultipartFile file, String urlSource) throws IOException {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(!ObjectUtils.isEmpty(urlSource) ? urlSource : FileConstants.DEFAULT_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
                throw  new IOException("Erro movendo o arquivo. Verificar pasta destino!");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean executeFile(String fileName) throws BusinessException {
        upload_folder = FileConstants.DEFAULT_FOLDER + fileName;
        boolean result = this.executeFiles();
        clearPathSource();
        return result;
    }


    @Override
    public boolean executeFiles() throws BusinessException {
        AtomicBoolean result = new AtomicBoolean(true);
        File tempSourceOrFile = new File(StringUtils.isEmpty(upload_folder) ? FileConstants.DEFAULT_FOLDER : upload_folder);
        SpreadsheetParser<ProductDTO> parser;
        String category;
        Queue<File> files = new LinkedList<>();
        if (tempSourceOrFile.exists()) {
            if(StringUtils.isEmpty(upload_folder))
                files.addAll(Arrays.asList(Objects.requireNonNull(tempSourceOrFile.listFiles())));
            else
                files.add(tempSourceOrFile);
        }
        for (File file : files) {
            try {
                List<String> errorMessages;
                parser = new SpreadsheetParser<>(ProductDTO.class, file);
                validateCategoryAndHeaderFile(parser, file.getName());
                category = parser.getLines().get(0).get(1).replace(",","").replace(".","");
                parser.setHeadersRows(2);
                List<ProductDTO> fileProducts = parser.parse();
                errorMessages = ValidatorFileUtil.validateColumns(fileProducts);
                if (errorMessages != null) {
                    String finalCategory = category;
                    fileProducts.forEach(productBean -> {
                                productBean.setCategory(finalCategory);
                                save(productBean);
                            }
                    );
                } else {
                    LOGGER.error(ERROR_PROCESS_EXECUTE);
                    AtomicReference<String> errorMessagesString = new AtomicReference<>("");
                    errorMessages.forEach(s -> errorMessagesString.set(errorMessagesString.get().concat(" | ").concat(errorMessagesString.get())));
                    throw new BusinessException(errorMessagesString.get());
                }
                uploadFileService.save(new UploadFile(file.getName(), errorMessages == null ? FAILED : SUCCESS));
            } catch (BusinessException e) {
                throw new BusinessException(e.getMessage());
            } catch (Exception e) {
                LOGGER.error("ERROR", e);
                result.set(false);
            }
        }
        return result.get();
    }

    @Override
    public ProductDTO entity(Long productId) throws DataNotFoundException {
        return buildProductDTO(productService.findById(productId).orElseThrow(
                () -> new DataNotFoundException(String.valueOf(productId),
                        ERROR_PRODUCT_NOT_FOUND)
        ));
    }

    @Override
    public List<ProductDTO> list() {
        List<ProductDTO> list = new ArrayList<>();
        productService.findAll().forEach(product -> list.add(buildProductDTO(product)));
        return list;
    }

    @Override
    public UploadFile getFileStatus(String fileName) {
        return uploadFileService.findByFileName(fileName);
    }

    @Override
    public ProductDTO save(ProductDTO product) {
        return buildProductDTO(productService.save(buildProductRepository(product)));
    }

    @Override
    public ProductDTO update(ProductDTO product) throws DataNotFoundException {
        entity(Long.valueOf(product.getLm()));
        return buildProductDTO(productService.save(buildProductRepository(product)));
    }

    @Override
    public boolean delete(ProductDTO product) throws DataNotFoundException{
        entity(Long.valueOf(product.getLm()));
        productService.delete(buildProductRepository(product));
        return true;
    }

    public void clearPathSource(){
        upload_folder = "";
    }

    private ProductDTO buildProductDTO(Product product){
        return ProductDTO.builder()
                .lm(String.valueOf(product.getLm()))
                .name(product.getName())
                .description(product.getDescription())
                .freeShipping(product.getFreeShipping())
                .price(product.getPrice())
                .category(product.getCategory()).build();
    }

    private boolean moveFilesToBeProcessed(String target, String source, String fileName) throws IOException{
        File inDirectory = new File(target + fileName);
        List<File> files = null;
        AtomicBoolean isSucess = new AtomicBoolean(true);
        if(inDirectory.exists()){
            if(StringUtils.isEmpty(fileName)) {
                files = Arrays.stream(Objects.requireNonNull(inDirectory.listFiles())).collect(Collectors.toList());
            } else {
                files = new ArrayList<>();
                files.add(inDirectory);
            }
        }
        files.forEach(f->{
            try {
                LOGGER.info(String.format("Movendo arquivo para pasta de processamento: %s", f.getName()));
                Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(!StringUtils.isEmpty(source) ?  source : upload_folder + f.getName()), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info(SUCCESS_MOVING_FILE);
            } catch (IOException e) {
                isSucess.set(false);
                LOGGER.error(ERROR_MOVING_FILE);
            }
        });

        if(!isSucess.get()) {
            throw new IOException(ERROR_MOVING_FILE);
        }

        if (files == null){
            return false;
        } else {
            return true;
        }
    }

    private boolean validateCategoryAndHeaderFile(SpreadsheetParser<ProductDTO> parser, String fileName) throws BusinessException{
        try {
            if (!"category".equalsIgnoreCase(parser.getLines().get(0).get(0)))
                throw new BusinessException(fileName + " - Categoria do produto obrigaroria.");
            if (!"lm".equalsIgnoreCase(parser.getLines().get(1).get(0)))
                throw new BusinessException(fileName + " - Header dos produtos obrigatorios na planilha..");
        } catch (IndexOutOfBoundsException e) {
            throw new BusinessException(fileName + " - Erro nos dados enviados. Category ou header errados!");
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return true;
    }
}