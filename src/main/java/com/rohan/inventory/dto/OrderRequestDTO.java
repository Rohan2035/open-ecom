package com.rohan.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {

    private String userEmail;
    private String address;
    private List<InnerOrderRequestDTO> orderRequests;

    @Data
    public static class InnerOrderRequestDTO {
        private String productName;
        private BigDecimal productPrice;
        private int productQuantity;
    }
}
