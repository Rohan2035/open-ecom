package com.rohan.ecom.service;

import com.rohan.ecom.dto.ProductRequestDTO;
import com.rohan.ecom.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
     List<ProductResponseDTO> getAllProduct();
     ProductResponseDTO getProductByProductName(String name);
     String addProduct(ProductRequestDTO requestDTO);
     String deleteProduct(String productName);
}
