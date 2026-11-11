package com.openecom.orders.service;

import com.openecom.events.OrderCreatedEvent;
import com.openecom.orders.dto.OrderItemsDTO;
import com.openecom.orders.dto.OrderRequestDTO;
import com.openecom.orders.entity.OrderDetails;
import com.openecom.orders.entity.OrderItems;
import com.openecom.orders.entity.ProductDetails;
import com.openecom.orders.exception.OrdersException;
import com.openecom.orders.repository.OrderDetailsRepository;
import com.openecom.orders.repository.OrderItemsRepository;
import com.openecom.orders.repository.ProductDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final String INITIATING = "INITIATING";

    private final OrderDetailsRepository orderDetailsRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderItemsRepository orderItemsRepository;

    @Autowired
    public OrderService(OrderDetailsRepository orderDetailsRepository,
                        ProductDetailsRepository productDetailsRepository,
                        KafkaTemplate<String, Object> kafkaTemplate,
                        OrderItemsRepository orderItemsRepository) {

        this.productDetailsRepository = productDetailsRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.orderItemsRepository = orderItemsRepository;
    }

    @Transactional
    public Map<String, String> createOrder(OrderRequestDTO orderRequestDTO) {
        String orderCode = createOrderCode(orderRequestDTO.getClientId());

        BigDecimal totalAmount = new BigDecimal(0);

        var productMap = fetchProductDetails(orderRequestDTO);

        for(var orderItems : orderRequestDTO.getOrderItems()) {
            BigDecimal amount = productMap.get(orderItems.getProductName());
            if(amount == null) throw new OrdersException("Product amount not found");

            amount = amount.multiply(new BigDecimal(orderItems.getProductQuantity()));
            totalAmount = totalAmount.add(amount);
        }

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderCode(orderCode);
        orderDetails.setOrderTimestamp(LocalDateTime.now());
        orderDetails.setStatus("CREATED");
        orderDetails.setClientId(orderRequestDTO.getClientId());
        orderDetails.setClientAddressId(orderRequestDTO.getClientAddressId());
        orderDetails.setTotalAmount(totalAmount);

        List<OrderItems> orderItems = mapToOrderItemsEntity(orderRequestDTO.getOrderItems(), orderCode, productMap);

        orderDetailsRepository.save(orderDetails);
        orderItemsRepository.saveAll(orderItems);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderCode(orderCode);
        event.setTotalAmount(totalAmount);
        event.setClientId(orderRequestDTO.getClientId());

         kafkaTemplate.send("order-created-topic", orderCode, event);

        return Map.of("status", "order successfully created");
    }

    private Map<String, BigDecimal> fetchProductDetails(OrderRequestDTO orderRequestDTO) {
        List<String> productNames = orderRequestDTO.getOrderItems()
                .stream()
                .map(OrderItemsDTO::getProductName)
                .collect(Collectors.toList());

        List<ProductDetails> products = productDetailsRepository.findByProductNameIn(productNames)
                .orElseThrow(() -> new RuntimeException("Products not found"));

        return products.stream()
                .collect(Collectors.toMap(ProductDetails::getProductName, ProductDetails::getProductPrice));
    }

    private static String createOrderCode(Long clientId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortUuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // Example result: ORD-123-20261101210100-A3F9B2
        return String.format("ORD-%d-%s-%s", clientId, timestamp, shortUuid);
    }

    private static List<OrderItems> mapToOrderItemsEntity(List<OrderItemsDTO> orderItemsDTOS,
                                                    String orderCode,
                                                    Map<String, BigDecimal> productMap) {

        List<OrderItems> orderItems = new ArrayList<>();
        for(OrderItemsDTO dto : orderItemsDTOS) {
            OrderItems orderItem = new OrderItems();

            orderItem.setOrderCode(orderCode);
            orderItem.setProductName(dto.getProductName());
            orderItem.setProductId(dto.getProductId());
            orderItem.setProductPrice(productMap.get(dto.getProductName()));
            orderItem.setQuantity(dto.getProductQuantity());

            orderItems.add(orderItem);
        }

        return orderItems;
    }


    // ==================== Temporary Listener ================================

}
