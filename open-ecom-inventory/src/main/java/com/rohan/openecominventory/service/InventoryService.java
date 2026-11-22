package com.rohan.openecominventory.service;

import com.openecom.constants.KafkaConstants;
import com.openecom.events.OrderCreatedEvent;
import com.openecom.events.OrderItemsEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = KafkaConstants.ORDER_CREATED_TOPIC,
            groupId = KafkaConstants.INVENTORY_SERVICE_GROUP
    )
    public void reserveInventory(OrderCreatedEvent event) {

    }
}
