package com.openecom.orders.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class OrderRequestDTO {

    private String userEmail;
    private String address;
    private List<InnerOrderRequestDTO> orderRequests;

    @Data
    public static class InnerOrderRequestDTO {
        private Long productId;
        private String productName;
        private BigDecimal productPrice;
        private int productQuantity;
    }
}

