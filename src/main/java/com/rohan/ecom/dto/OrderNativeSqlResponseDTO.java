package com.rohan.ecom.dto;

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


    public OrderNativeSqlResponseDTO() {}

    // Todo - Remove these
    public OrderNativeSqlResponseDTO(String orderedBy, LocalDate orderedOn, String orderStatus, String orderCode,
                                     Integer quantity, String address, BigDecimal price, String productName,
                                     String productDescription, String productCategory) {
        this.orderedBy = orderedBy;
        this.orderedOn = orderedOn;
        this.orderStatus = orderStatus;
        this.orderCode = orderCode;
        this.quantity = quantity;
        this.address = address;
        this.price = price;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
    }
}
