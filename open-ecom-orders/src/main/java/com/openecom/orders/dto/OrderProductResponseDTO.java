package com.openecom.orders.dto;

public record OrderProductResponseDTO (
        String productName,
        String productDescription,
        String productCategory,
        String productPrice,
        int productQuantity
){}
