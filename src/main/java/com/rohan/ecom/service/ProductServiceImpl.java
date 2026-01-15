package com.rohan.ecom.service;

import com.rohan.ecom.dto.ProductRequestDTO;
import com.rohan.ecom.dto.ProductResponseDTO;
import com.rohan.ecom.entity.Product;
import com.rohan.ecom.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductResponseDTO> getAllProduct() {
        List<Product> productList = productRepository.findAll();

        return productList.stream()
                .map(this::productResponseDTOMapper)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductByProductName(String productName) {
        Product product = productRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        return this.productResponseDTOMapper(product);
    }

    @Override
    public String addProduct(ProductRequestDTO requestDTO) {
        Product product = this.productMapper(requestDTO);
        try {
            productRepository.save(product);
        } catch (Exception e) {
            LOG.error("Exception: ", e);
            return "Failed";
        }

        return "Success";
    }

    @Override
    public String deleteProduct(String productName) {
        try {
            productRepository.findByProductName(productName);
        } catch (Exception e) {
            LOG.error("Exception: ", e);
            return "Failed";
        }

        return "Success";
    }

    protected ProductResponseDTO productResponseDTOMapper(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setProductName(product.getProductName());
        productResponseDTO.setProductDescription(product.getProductDescription());
        productResponseDTO.setProductCategory(product.getProductCategory());
        productResponseDTO.setProductPrice(product.getProductPrice());
        productResponseDTO.setProductQuantity(product.getProductQuantity());

        return productResponseDTO;
    }

    protected Product productMapper(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setProductName(productRequestDTO.getProductName());
        product.setProductPrice(productRequestDTO.getProductPrice());
        product.setProductCategory(productRequestDTO.getProductCategory());
        product.setProductDescription(productRequestDTO.getProductDescription());
        product.setProductQuantity(productRequestDTO.getProductQuantity());

        return product;
    }
}
