package com.openecom.orders.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderResponseDTO (
        String orderCode,
        LocalDate orderDate,
        List<OrderProductResponseDTO> products
){}
