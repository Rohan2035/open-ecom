package com.rohan.openecominventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_stocks")
@Getter
@Setter
public class ProductStocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long Id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_quantity")
    private Long productQuantity;

    @Column(name = "product_reserved_quantity")
    private Long productReservedQuantity;
}