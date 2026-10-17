package com.openecom.constants;


public final class KafkaConstants {

    private KafkaConstants() {}

    // Topics
    public static final String ORDER_CREATED_TOPIC = "order-created-topic";

    // Consumer Groups
    public static final String INVENTORY_SERVICE_GROUP = "inventory-service-group";
    public static final String PAYMENT_SERVICE_GROUP = "payment-service-group";
}
