package com.openecom.events;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String orderCode;
    private String userCode;
    private List<OrderItemDTO> orderList;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
}
