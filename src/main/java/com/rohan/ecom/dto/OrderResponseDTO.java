package com.rohan.ecom.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponseDTO {
    private String orderCode;
    private LocalDate orderDate;
    List<OrderProductResponseDTO> products;
}
