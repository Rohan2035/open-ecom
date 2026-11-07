package com.openecom.orders.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_details")
@Getter
@Setter
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderDetailsId;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_address_id")
    private Long clientAddressId;

    @Column(name = "order_timestamp")
    private LocalDateTime orderTimestamp;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "status")
    private String status;
}
