package com.openecom.ecom.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductListDTO {
    private String status;
    List<ProductResponseDTO> products;
}
