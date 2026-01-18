package com.rohan.ecom.service;

import com.rohan.ecom.dto.OrderNativeSqlResponseDTO;
import com.rohan.ecom.dto.OrderProductResponseDTO;
import com.rohan.ecom.dto.OrderRequestDTO;
import com.rohan.ecom.dto.OrderResponseDTO;
import com.rohan.ecom.dto.ViewOrderResponseDTO;
import com.rohan.ecom.dto.ViewOrderRequestDTO;
import com.rohan.ecom.dto.compositekey.OrderKey;
import com.rohan.ecom.entity.Order;
import com.rohan.ecom.entity.Product;
import com.rohan.ecom.entity.User;
import com.rohan.ecom.exceptions.OpenEcomException;
import com.rohan.ecom.exceptions.ProductNotFoundException;
import com.rohan.ecom.exceptions.ProductQuantityExceededException;
import com.rohan.ecom.exceptions.UserDetailsNotFoundException;
import com.rohan.ecom.repository.OrderRepository;
import com.rohan.ecom.repository.ProductRepository;
import com.rohan.ecom.repository.UserDetailsRepository;
import com.rohan.ecom.repository.ViewOrderDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String USER_NOT_FOUND_MSG = "User Details not found for email: ";
    private static final String PRODUCT_QUANTITY_MSG = "Product Quantity Exceeded for product: ";
    private static final String PRODUCT_NOT_FOUND = "Product not found: ";
    private static final String ORDER_NOT_FOUND = "Sorry, No Orders Found!";
    private static final String EMPTY_STRING = "";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";

    private final OrderRepository orderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ProductRepository productRepository;
    private final ViewOrderDetailsRepository viewOrderDetailsRepository;
    private final ExecutorService pool;
    private final PaymentService paymentService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserDetailsRepository userDetailsRepository,
                            ProductRepository productRepository, ViewOrderDetailsRepository viewOrderDetailsRepository,
                            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.productRepository = productRepository;
        this.viewOrderDetailsRepository = viewOrderDetailsRepository;
        this.paymentService = paymentService;
        this.pool = Executors.newFixedThreadPool(1);
    }

    @Override
    @Transactional
    public String addOrder(OrderRequestDTO orderRequestDTO) {
        User user = userDetailsRepository.findByEmail(orderRequestDTO.getUserEmail())
                .orElseThrow(() -> new UserDetailsNotFoundException(USER_NOT_FOUND_MSG +
                        orderRequestDTO.getUserEmail()));

        Future<String> futureOrderCode = pool.submit(() -> generateOrderCode(user.getFirstName()));

        List<Order> orders = new ArrayList<>();
        for(OrderRequestDTO.InnerOrderRequestDTO orderRequests : orderRequestDTO.getOrderRequests()) {
            Product product = productRepository.findByProductName(orderRequests.getProductName())
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND + orderRequests.getProductName()));

            int checkUpdates = productRepository.decrementProductQuantity(product.getProductId(), orderRequests.getProductQuantity());
            if(checkUpdates == 0) {
                throw new ProductQuantityExceededException(PRODUCT_QUANTITY_MSG + orderRequests.getProductName());
            }

            String orderCode;
            try {
                orderCode = futureOrderCode.get();
            } catch(Exception e) {
                throw new OpenEcomException(e.getMessage());
            }

            Order order = this.mapOrder(orderRequests, user, product.getProductId(), orderCode, orderRequestDTO.getAddress());
            orders.add(order);
        }

        // Payment Gateway Logic
        String response = paymentService.makePayment();

        if(response.equals(SUCCESS)) {
            orderRepository.saveAll(orders);
        } else {
            response = FAIL;
        }

        return response;
    }

    @Override
    public String deleteOrder(OrderRequestDTO orderRequestDTO) {
        return "";
    }

    @Override
    public ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO) {
        List<OrderNativeSqlResponseDTO> sqlResponse = viewOrderDetailsRepository.fetchOrders(
                orderRequestDTO.getUserEmail(),
                orderRequestDTO.getOrderDate(),
                orderRequestDTO.getOrderCode()
        );

        return this.mapViewOrderDTO(sqlResponse);
    }

    @Override
    public List<ViewOrderResponseDTO> viewAllOrder(String username) {
        return null;
    }

    protected Order mapOrder(OrderRequestDTO.InnerOrderRequestDTO orderRequestDTO, User user,
            Integer productId, String orderCode, String address) {

        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(orderCode);
        order.setOrderAddress(address);
        order.setProductId(productId);
        order.setProductQuantity(orderRequestDTO.getProductQuantity());
        order.setProductPrice(orderRequestDTO.getProductPrice().multiply(BigDecimal.valueOf(orderRequestDTO.getProductQuantity())));
        order.setOrderDate(LocalDate.now());
        order.setOrderTime(LocalDateTime.now());
        return order;
    }

    private static String generateOrderCode(String username) {
        String randomCode = UUID.randomUUID().toString()
                .substring(0, 3)
                .replace("_", "");

        String localDate = LocalDate.now().toString().replace("-", "");

        if(username.length() > 5) {
            username = username.substring(0, 3);
        }

        return username.toLowerCase() + localDate + randomCode;
    }

    protected ViewOrderResponseDTO mapViewOrderDTO(List<OrderNativeSqlResponseDTO> sqlResponse) {
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

            mapAndSetOrderProductResponseDTO(sqlResponse, orderResponseDTO);

            orderResponseList.add(orderResponseDTO);
        }

        responseDTO.setOrders(orderResponseList);
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
}
