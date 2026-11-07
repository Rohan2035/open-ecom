package com.openecom.orders.service;

import com.openecom.orders.dto.OrderItemsDTO;
import com.openecom.orders.dto.OrderRequestDTO;
import com.openecom.orders.entity.OrderDetails;
import com.openecom.orders.entity.ProductDetails;
import com.openecom.orders.repository.OrderDetailsRepository;
import com.openecom.orders.repository.OrderItemsRepository;
import com.openecom.orders.repository.ProductDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


    public Map<String, String> createOrder(OrderRequestDTO orderRequestDTO) {
        String orderCode = createOrderCode(orderRequestDTO.getClientId());

        BigDecimal totalAmount = new BigDecimal(0);

        var productMap = fetchProductDetails(orderRequestDTO);

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
}
