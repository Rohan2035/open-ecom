package com.rohan.ecom.dto;

import lombok.Data;

@Data
public class OrderProductResponseDTO {
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productPrice;
    private int productQuantity;
}
