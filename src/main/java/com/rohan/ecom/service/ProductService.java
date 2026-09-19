package com.openecom.ecom.service;

import com.openecom.ecom.dto.ProductListDTO;
import com.openecom.ecom.dto.ProductResponseDTO;

public interface ProductService {
     ProductResponseDTO getProductByProductName(String name);
     ProductListDTO getProductSuggestion();
}
