package com.openecom.ecom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "quantity_details")
@Getter
@Setter
public class Quantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_quantity_id")
    private Long id;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name ="product_quantity")
    private Integer productQuantity;
}
