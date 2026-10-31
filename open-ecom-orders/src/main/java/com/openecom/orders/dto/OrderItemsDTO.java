package com.openecom.orders.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsDTO {
    private long productId;
    private long productQuantity;
    private String productName;
}
