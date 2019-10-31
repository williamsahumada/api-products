package com.test.api.product.utils;

import com.test.api.product.constants.FileProductRepositoryConstants;
import com.test.api.product.constants.Messages;
import com.test.api.product.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ValidatorFileUtil {

    private ValidatorFileUtil(){}

    public static List<String> validateColumns(List<ProductDTO> productList){
        List<String> errorMessages = new ArrayList<>();
        String category = productList.get(0).getLm().toString();
        ProductDTO headerProduct = productList.get(1);
        if(!FileProductRepositoryConstants.COLUMN_IM.equalsIgnoreCase(headerProduct.getLm().toString())){
            errorMessages.add(String.format("Coluna %s não encontrada.", FileProductRepositoryConstants.COLUMN_IM));
        }
        if(!FileProductRepositoryConstants.COLUMN_NAME.equalsIgnoreCase(headerProduct.getName())){
            errorMessages.add(String.format(Messages.NOT_FOUND_COLUMN_GENERIC_MESSAGE, FileProductRepositoryConstants.COLUMN_NAME));
        }
        if(!FileProductRepositoryConstants.COLUMN_DESCRIPTION.equalsIgnoreCase(headerProduct.getDescription())){
            errorMessages.add(String.format(Messages.NOT_FOUND_COLUMN_GENERIC_MESSAGE, FileProductRepositoryConstants.COLUMN_DESCRIPTION));
        }
        if(!FileProductRepositoryConstants.COLUMN_CATEGORY.equalsIgnoreCase(category)){
            errorMessages.add(String.format(Messages.NOT_FOUND_COLUMN_GENERIC_MESSAGE, FileProductRepositoryConstants.COLUMN_CATEGORY));
        }
        if(!FileProductRepositoryConstants.COLUMN_PRICE.equalsIgnoreCase(headerProduct.getPrice())){
            errorMessages.add(String.format(Messages.NOT_FOUND_COLUMN_GENERIC_MESSAGE, FileProductRepositoryConstants.COLUMN_PRICE));
        }
        if(!FileProductRepositoryConstants.COLUMN_FREE_SHIPPING.equalsIgnoreCase(headerProduct.getFreeShipping())){
            errorMessages.add(String.format(Messages.NOT_FOUND_COLUMN_GENERIC_MESSAGE, FileProductRepositoryConstants.COLUMN_FREE_SHIPPING));
        }
        return errorMessages;
    }

}
