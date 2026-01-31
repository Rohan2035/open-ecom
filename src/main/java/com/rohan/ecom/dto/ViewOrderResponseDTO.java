package com.rohan.ecom.dto;

import lombok.Data;

import java.util.List;

@Data
public class ViewOrderResponseDTO {
    private int status;
    private String statusMessage;
    private String orderedBy;
    private List<OrderResponseDTO> orders;
}
