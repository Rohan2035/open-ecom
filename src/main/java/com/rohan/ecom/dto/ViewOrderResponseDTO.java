package com.rohan.ecom.dto;

import lombok.Data;

import java.util.List;

@Data
public class ViewOrderResponseDTO {
    private String orderedBy;
    private List<OrderResponseDTO> orders;
}
