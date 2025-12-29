package com.rohan.inventory.service;

import com.rohan.inventory.dto.OrderRequestDTO;
import com.rohan.inventory.dto.OrderResponseDTO;
import com.rohan.inventory.dto.ViewOrderRequestDTO;
import com.rohan.inventory.entity.Order;
import com.rohan.inventory.entity.Product;
import com.rohan.inventory.entity.User;
import com.rohan.inventory.exceptions.ProductNotFoundException;
import com.rohan.inventory.exceptions.ProductQuantityExceededException;
import com.rohan.inventory.exceptions.UserDetailsNotFoundException;
import com.rohan.inventory.repository.OrderRepository;
import com.rohan.inventory.repository.ProductRepository;
import com.rohan.inventory.repository.UserDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String USER_NOT_FOUND_MSG = "User Details not found for email: ";
    private static final String PRODUCT_QUANTITY_MSG = "Product Quantity Exceeded for product: ";
    private static final String PRODUCT_NOT_FOUND = "Product not found: ";

    private final OrderRepository orderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserDetailsRepository userDetailsRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public String addOrder(OrderRequestDTO orderRequestDTO) {
        User user = userDetailsRepository.findByEmail(orderRequestDTO.getUserEmail())
                .orElseThrow(() -> new UserDetailsNotFoundException(USER_NOT_FOUND_MSG +
                        orderRequestDTO.getUserEmail()));

        List<Order> orders = new ArrayList<>();
        for(OrderRequestDTO.InnerOrderRequestDTO orderRequests : orderRequestDTO.getOrderRequests()) {
            Product product = productRepository.findByProductName(orderRequests.getProductName())
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND + orderRequests.getProductName()));

            int checkUpdates = productRepository.decrementProductQuantity(product.getProductId(),
                    orderRequests.getProductQuantity());
            if(checkUpdates == 0) {
                throw new ProductQuantityExceededException(PRODUCT_QUANTITY_MSG + orderRequests.getProductName());
            }

            Order order = this.mapOrder(orderRequests, user, product.getProductId());
            orders.add(order);
        }

        orderRepository.saveAll(orders);

        return "Success";
    }

    @Override
    public String deleteOrder(OrderRequestDTO orderRequestDTO) {
        return "";
    }

    @Override
    public List<OrderResponseDTO> viewOrder(ViewOrderRequestDTO orderRequestDTO) {
        return null;
    }

    @Override
    public List<OrderResponseDTO> viewAllOrder(String username) {
        return null;
    }

    protected Order mapOrder(OrderRequestDTO.InnerOrderRequestDTO orderRequestDTO, User user, Integer productId) {
        Order order = new Order();
        order.setUser(user);
        order.setProductId(productId);
        order.setProductQuantity(orderRequestDTO.getProductQuantity());
        order.setProductPrice(orderRequestDTO.getProductPrice().multiply(BigDecimal.valueOf(orderRequestDTO.getProductQuantity())));
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    protected OrderResponseDTO mapOrderResponse(Order order) {
        return null;
    }
}
