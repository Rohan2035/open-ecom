package com.rohan.inventory.service;

import com.rohan.inventory.dto.OrderNativeSqlResponseDTO;
import com.rohan.inventory.dto.OrderProductResponseDTO;
import com.rohan.inventory.dto.OrderRequestDTO;
import com.rohan.inventory.dto.OrderResponseDTO;
import com.rohan.inventory.dto.ViewOrderResponseDTO;
import com.rohan.inventory.dto.ViewOrderRequestDTO;
import com.rohan.inventory.dto.compositekey.OrderKey;
import com.rohan.inventory.entity.Order;
import com.rohan.inventory.entity.Product;
import com.rohan.inventory.entity.User;
import com.rohan.inventory.entity.ViewOrder;
import com.rohan.inventory.exceptions.OrderNotFoundException;
import com.rohan.inventory.exceptions.ProductNotFoundException;
import com.rohan.inventory.exceptions.ProductQuantityExceededException;
import com.rohan.inventory.exceptions.UserDetailsNotFoundException;
import com.rohan.inventory.repository.OrderRepository;
import com.rohan.inventory.repository.ProductRepository;
import com.rohan.inventory.repository.UserDetailsRepository;
import com.rohan.inventory.repository.ViewOrderDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String USER_NOT_FOUND_MSG = "User Details not found for email: ";
    private static final String PRODUCT_QUANTITY_MSG = "Product Quantity Exceeded for product: ";
    private static final String PRODUCT_NOT_FOUND = "Product not found: ";
    private static final String ORDER_NOT_FOUND = "Sorry, No Orders Found!";

    private final OrderRepository orderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ProductRepository productRepository;
    private final ViewOrderDetailsRepository viewOrderDetailsRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserDetailsRepository userDetailsRepository,
                            ProductRepository productRepository, ViewOrderDetailsRepository viewOrderDetailsRepository) {
        this.orderRepository = orderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.productRepository = productRepository;
        this.viewOrderDetailsRepository = viewOrderDetailsRepository;
    }

    @Override
    @Transactional
    public String addOrder(OrderRequestDTO orderRequestDTO) {
        User user = userDetailsRepository.findByEmail(orderRequestDTO.getUserEmail())
                .orElseThrow(() -> new UserDetailsNotFoundException(USER_NOT_FOUND_MSG +
                        orderRequestDTO.getUserEmail()));

        String orderCode = generateOrderCode(user.getFirstName());

        List<Order> orders = new ArrayList<>();
        for(OrderRequestDTO.InnerOrderRequestDTO orderRequests : orderRequestDTO.getOrderRequests()) {
            Product product = productRepository.findByProductName(orderRequests.getProductName())
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND + orderRequests.getProductName()));

            int checkUpdates = productRepository.decrementProductQuantity(product.getProductId(),
                    orderRequests.getProductQuantity());
            if(checkUpdates == 0) {
                throw new ProductQuantityExceededException(PRODUCT_QUANTITY_MSG + orderRequests.getProductName());
            }

            Order order = this.mapOrder(orderRequests, user, product.getProductId(), orderCode, orderRequestDTO.getAddress());
            orders.add(order);
        }

        // Payment Gateway Logic

        orderRepository.saveAll(orders);

        return "Success";
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

        return null;
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
}
