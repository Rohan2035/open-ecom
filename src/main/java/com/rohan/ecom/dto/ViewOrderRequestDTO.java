package com.rohan.ecom.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ViewOrderRequestDTO {
    private String userEmail;
    private String orderCode;
    private LocalDate orderDate;
}
