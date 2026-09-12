package com.openecom.orders.dto;

import java.time.LocalDate;
import java.util.List;

public record ViewOrderResponseDTO (
         int status,
         String statusMessage,
         String orderedBy,
         List<OrderResponseDTO> orders
){}
