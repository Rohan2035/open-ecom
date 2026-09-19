package com.openecom.ecom.service;

import com.openecom.ecom.dto.ProductListDTO;
import com.openecom.ecom.dto.ProductResponseDTO;
import com.openecom.ecom.entity.Product;
import com.openecom.ecom.exceptions.ProductNotFoundException;
import com.openecom.ecom.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private static final String PRODUCT_ID_NOT_FOUND = "Product not found for id: ";
    private static final String PRODUCT_NAME_NOT_FOUND = "Product not found for name: ";

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO getProductByProductName(String productName) {
        Product product = productRepository.findByProductName(productName)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NAME_NOT_FOUND + productName));

        return this.productResponseDTOMapper(product);
    }

    @Override
    public ProductListDTO getProductSuggestion() {
        // Todo - Future - API Calls that will run python scripts and suggest products
        // Returns 20 products in the DB

        ProductListDTO productListDTO = new ProductListDTO();

        List<Product> products = productRepository.findTop20By()
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));


        if(products.isEmpty()) {
            productListDTO.setStatus("Products Not Found");
            return productListDTO;
        }

        List<ProductResponseDTO> productResponseDTOS = products.stream()
                .map(this::productResponseDTOMapper)
                .toList();

        productListDTO.setProducts(productResponseDTOS);
        productListDTO.setStatus("Success");

        return productListDTO;
    }


    protected ProductResponseDTO productResponseDTOMapper(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setProductName(product.getProductName());
        productResponseDTO.setProductDescription(product.getProductDescription());
        productResponseDTO.setProductCategory(product.getProductCategory());
        productResponseDTO.setProductPrice(product.getProductPrice());

        return productResponseDTO;
    }
}
