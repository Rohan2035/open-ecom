package com.rohan.inventory.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class ViewOrderResponseDTO {
    private String orderedBy;
    private List<OrderResponseDTO> orders;
}
