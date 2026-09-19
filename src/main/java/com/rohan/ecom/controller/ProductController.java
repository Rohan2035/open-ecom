package com.openecom.ecom.controller;

import com.openecom.ecom.dto.ProductListDTO;
import com.openecom.ecom.dto.ProductResponseDTO;
import com.openecom.ecom.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/getproductname")
    public ProductResponseDTO getProductName(@RequestParam("productName") String productName) {
        ProductResponseDTO responseDTO;
        LOG.info("=== Fetching Products ===");
        Long startTime = System.currentTimeMillis();

        responseDTO = productService.getProductByProductName(productName);

        Long endTime = System.currentTimeMillis();
        LOG.info("GET Product - Total time taken: {}ms", (endTime - startTime));

        return responseDTO;
    }

    @GetMapping("/productsuggestion")
    public ProductListDTO getProductSuggestions() {
        ProductListDTO productListDTO;

        LOG.info("=== Fetching Product Suggestions ===");
        long startTime = System.currentTimeMillis();

        productListDTO = productService.getProductSuggestion();

        long endTime = System.currentTimeMillis();
        LOG.info("GET Product Suggestions - Time Taken: {}ms", (endTime - startTime));
        return productListDTO;
    }
}
