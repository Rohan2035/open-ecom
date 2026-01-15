package com.rohan.ecom.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponseDTO {
    private String productName;
    private String productDescription;
    private String productCategory;
    private BigDecimal productPrice;
    private int productQuantity;
}