package com.openecom.events;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String orderCode;
    private Long clientId;
    private List<OrderItemsEventDTO> orderList;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;


    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(orderCode);
        joiner.add(clientId.toString());
        joiner.add(totalAmount.toString());

        return "OrderCreatedEvent{" + joiner + "}";
    }
}
