package com.openecom.ecom.service;

import com.openecom.ecom.component.OrderComponent;
import com.openecom.ecom.dto.OrderNativeSqlResponseDTO;
import com.openecom.ecom.dto.OrderProductResponseDTO;
import com.openecom.ecom.dto.OrderRequestDTO;
import com.openecom.ecom.dto.OrderResponseDTO;
import com.openecom.ecom.dto.ViewOrderRequestDTO;
import com.openecom.ecom.dto.ViewOrderResponseDTO;
import com.openecom.ecom.dto.compositekey.OrderKey;
import com.openecom.ecom.entity.Order;
import com.openecom.ecom.entity.Product;
import com.openecom.ecom.entity.User;
import com.openecom.ecom.exceptions.OpenEcomException;
import com.openecom.ecom.exceptions.ProductQuantityExceededException;
import com.openecom.ecom.exceptions.UserDetailsNotFoundException;
import com.openecom.ecom.repository.OrderRepository;
import com.openecom.ecom.repository.ProductRepository;
import com.openecom.ecom.repository.UserDetailsRepository;
import com.openecom.ecom.repository.ViewOrderDetailsRepository;
import com.openecom.ecom.util.Codes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String PRODUCT_NOT_FOUND = "Product not found: ";
    private static final String ORDER_NOT_FOUND = "Sorry, No Orders Found!";
    private static final String EMPTY_STRING = "";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ViewOrderDetailsRepository viewOrderDetailsRepository;
    private final PaymentService paymentService;
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    private OrderComponent orderComponent;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            ViewOrderDetailsRepository viewOrderDetailsRepository,
                            PaymentService paymentService,
                            UserDetailsRepository userDetailsRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.viewOrderDetailsRepository = viewOrderDetailsRepository;
        this.paymentService = paymentService;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public Map<String, String> createOrder(OrderRequestDTO orderRequestDTO) {
        Map<String, String> orderStatus = new HashMap<>();
        String orderCode = generateOrderCode();

        LOG.info("Creating order for order code: {}", orderCode);

        // Fetch userId
        Integer userId = fetchUserId(orderRequestDTO.getUserEmail());

        List<Order> orders = new ArrayList<>();

        // Reserve product quantity
        for(OrderRequestDTO.InnerOrderRequestDTO requests : orderRequestDTO.getOrderRequests()) {
            try {
                orderComponent.reserveProductQuantities(requests.getProductId(), requests.getProductQuantity());

                // Will create an address table for multiple addresses
                Order order  = mapOrder(requests, orderCode, "address", userId);
                orders.add(order);

            } catch(ProductQuantityExceededException e) {
                // We can make this asynchronous
                LOG.info("Product quantity exceeded for the order code: {}", orderCode);
                orderComponent.releaseProductQuantity(orderRequestDTO.getOrderRequests());
                orderStatus.put(requests.getProductName(), "Quantity Exceeded");

                break;
            }
        }

        if(!orderStatus.isEmpty()) {
            return orderStatus;
        }

        // Make Payment
        String paymentResult = paymentService.makePayment();

        if("SUCCESS".equalsIgnoreCase(paymentResult)) {
            // Confirm Product quantity
            try {
                orderComponent.confirmProductQuantity(orderRequestDTO.getOrderRequests());

            } catch(Exception e) {
                // Release the reserved product quantity -> Can be done asynchronously
                orderComponent.releaseProductQuantity(orderRequestDTO.getOrderRequests());
                String refundStatus = paymentService.refundPayment();
                orderStatus.put("Refund Status", refundStatus);
                return orderStatus;
            }
        } else {
            orderComponent.releaseProductQuantity(orderRequestDTO.getOrderRequests());
            String refundStatus = paymentService.refundPayment();
            orderStatus.put("Refund Status", refundStatus);
            return orderStatus;
        }

        try {
            orderRepository.saveAll(orders);
        } catch(Exception e) {
            orderComponent.rollbackQuantity(orderRequestDTO.getOrderRequests());
            String refundStatus = paymentService.refundPayment();
            orderStatus.put("Refund Status", refundStatus);
            return orderStatus;
        }

        orderStatus.put("Status", "SUCCESS");
        return orderStatus;
    }

    @Override
    public ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO) {
        ViewOrderResponseDTO responseDTO;

        List<OrderNativeSqlResponseDTO> sqlResponse = viewOrderDetailsRepository.fetchOrders(
                orderRequestDTO.getUserEmail(),
                orderRequestDTO.getOrderDate(),
                orderRequestDTO.getOrderCode()
        );

        if(CollectionUtils.isEmpty(sqlResponse)) {
            LOG.info("Order is Empty");
            responseDTO = new ViewOrderResponseDTO();
            responseDTO.setStatus(Codes.FAIL.getCode());
            responseDTO.setStatusMessage(ORDER_NOT_FOUND);
            responseDTO.setOrders(List.of());
            responseDTO.setOrderedBy(EMPTY_STRING);
            return responseDTO;
        }

        LOG.info("Order service - successfully fetched orders");
        responseDTO = this.mapViewOrderDTO(sqlResponse);
        responseDTO.setStatus(Codes.SUCCESS.getCode());
        responseDTO.setStatusMessage("Successfully Fetched " + responseDTO.getOrders().size() + " Orders");
        return responseDTO;
    }

    protected Order mapOrder(OrderRequestDTO.InnerOrderRequestDTO orderRequestDTO,
                             String orderCode,
                             String address,
                             Integer userId) {

        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setUserId(userId);
        order.setOrderAddress(address);
        order.setProductId(orderRequestDTO.getProductId());
        order.setProductQuantity(orderRequestDTO.getProductQuantity());
        order.setProductPrice(orderRequestDTO.getProductPrice().multiply(BigDecimal.valueOf(orderRequestDTO.getProductQuantity())));
        order.setOrderDate(LocalDate.now());
        order.setOrderTime(LocalDateTime.now());
        return order;
    }


    private static String generateOrderCode() {
        long timestamp = System.currentTimeMillis();
        int randomDigits = ThreadLocalRandom.current().nextInt(100, 1000);

        return "ORD-" + timestamp + "-" + randomDigits;
    }

    protected ViewOrderResponseDTO mapViewOrderDTO(List<OrderNativeSqlResponseDTO> sqlResponse) {
        long startTime = System.currentTimeMillis();
        ViewOrderResponseDTO responseDTO = new ViewOrderResponseDTO();
        if(sqlResponse == null || sqlResponse.isEmpty()) {
            return null;
        }

        responseDTO.setOrderedBy(sqlResponse.get(0).getOrderedBy());
        Map<OrderKey, List<OrderNativeSqlResponseDTO>> orderMap = sqlResponse.stream()
                .collect(Collectors.groupingBy(order -> new OrderKey(
                        order.getOrderCode(), order.getOrderedOn())));

        List<OrderResponseDTO> orderResponseList = new ArrayList<>();
        for(OrderKey orderKey : orderMap.keySet()) {
            OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
            orderResponseDTO.setOrderCode(orderKey.orderCode());
            orderResponseDTO.setOrderDate(orderKey.orderDate());

            mapAndSetOrderProductResponseDTO(orderMap.get(orderKey), orderResponseDTO);

            orderResponseList.add(orderResponseDTO);
        }

        responseDTO.setOrders(orderResponseList);
        long endTime = System.currentTimeMillis();
        LOG.info("Time taken to map the orders: {} ms", (endTime - startTime));
        return responseDTO;
    }

    private void mapAndSetOrderProductResponseDTO(List<OrderNativeSqlResponseDTO> sqlResponse, OrderResponseDTO orderResponseDTO) {
        List<OrderProductResponseDTO> productList = new ArrayList<>();
        for(OrderNativeSqlResponseDTO product : sqlResponse) {
            OrderProductResponseDTO responseDTO = new OrderProductResponseDTO();
            responseDTO.setProductName(product.getProductName());
            responseDTO.setProductPrice(product.getPrice().toString());
            responseDTO.setProductQuantity(product.getQuantity());
            responseDTO.setProductCategory(product.getProductCategory());
            responseDTO.setProductDescription(product.getProductDescription());
            productList.add(responseDTO);
        }
        orderResponseDTO.setProducts(productList);
    }

    protected Map<String, Product> fetchProductMap(Set<String> productNames) {
        LOG.info("Fetching Products");
        long startTime = System.currentTimeMillis();
        Map<String, Product> productMap = productRepository.findByProductNameIn(productNames)
                .orElseThrow(() -> new OpenEcomException(PRODUCT_NOT_FOUND))
                .stream()
                .collect(Collectors.toMap(Product::getProductName, Function.identity()));
        long endTime = System.currentTimeMillis();
        LOG.info("Products Fetched in {} ms", (endTime - startTime));

        return productMap;
    }

    protected Integer fetchUserId(String email) {
        User user = userDetailsRepository.findByEmail(email)
                .orElseThrow(() -> new UserDetailsNotFoundException("user not found"));

        return user.getUserId();
    }
}
