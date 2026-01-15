package com.rohan.ecom.controller;

import com.rohan.ecom.dto.ProductRequestDTO;
import com.rohan.ecom.dto.ProductResponseDTO;
import com.rohan.ecom.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProduct();
    }

    @GetMapping("/getproductname")
    public ProductResponseDTO getProductName(@RequestParam("productName") String productName) {
        return productService.getProductByProductName(productName);
    }

    @PostMapping("/addproduct")
    public Map<String, String> addProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        String response =  productService.addProduct(productRequestDTO);
        return Map.of("Status", response);
    }

    @DeleteMapping("/deleteproduct")
    public Map<String, String> deleteProduct(@RequestParam("productName") String productName) {
        String response = productService.deleteProduct(productName);
        return Map.of("Status", response);
    }
}
