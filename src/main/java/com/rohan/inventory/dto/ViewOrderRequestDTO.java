package com.rohan.inventory.dto;

import lombok.Data;

@Data
public class ViewOrderRequestDTO {
    private String userEmail;
    private String orderCode;
    private Integer limit;
}
