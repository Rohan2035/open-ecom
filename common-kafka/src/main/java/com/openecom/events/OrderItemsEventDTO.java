package com.openecom.events;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemsEventDTO {
    private Long productId;
    private Long productQuantity;
}
