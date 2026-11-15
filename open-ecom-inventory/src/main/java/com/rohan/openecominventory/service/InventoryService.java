package com.rohan.openecominventory.service;

import com.openecom.constants.KafkaConstants;
import com.openecom.events.OrderCreatedEvent;
import com.openecom.events.OrderItemsEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {

    @KafkaListener(
            topics = KafkaConstants.ORDER_CREATED_TOPIC,
            groupId = KafkaConstants.INVENTORY_SERVICE_GROUP
    )
    public void reserveInventory(OrderCreatedEvent event) {
        log.info("<============= Event Created ================>");
        log.info("{}", event);

        StringBuilder builder = new StringBuilder();
        for(OrderItemsEventDTO items : event.getOrderList()) {
            builder.append(items.getProductId()).append(" ");
        }

        log.info("Products: {}", builder);
    }
}
