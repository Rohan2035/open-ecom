package com.rohan.ecom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_details")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "product_quantity")
    private Integer productQuantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;
}
