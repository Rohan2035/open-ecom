package com.openecom.orders.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    private Long clientId;
    private Long clientAddressId;
    private List<OrderItemsDTO> orderItems;
}
