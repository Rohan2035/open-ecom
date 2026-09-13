package com.openecom.orders.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OrderNativeSqlResponseDTO {

    private String orderedBy;
    private LocalDate orderedOn;
    private String orderStatus;
    private String orderCode;
    private Integer quantity;
    private String address;
    private BigDecimal price;
    private String productName;
    private String productDescription;
    private String productCategory;
}
