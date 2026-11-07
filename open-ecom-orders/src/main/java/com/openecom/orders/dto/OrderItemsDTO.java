package com.openecom.orders.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsDTO {
    private Long productId;
    private Long productQuantity;
    private String productName;
}
