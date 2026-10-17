package com.openecom.events;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    private Long productId;
    private BigDecimal productPrice;
    private Long productQuantity;
}
