package com.rohan.inventory.service;

import com.rohan.inventory.DTO.OrderRequestDTO;
import com.rohan.inventory.DTO.OrderResponseDTO;
import com.rohan.inventory.entity.Order;
import com.rohan.inventory.entity.Product;
import com.rohan.inventory.entity.User;
import com.rohan.inventory.repository.OrderRepository;
import com.rohan.inventory.repository.ProductRepository;
import com.rohan.inventory.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public String addOrder(OrderRequestDTO orderRequestDTO) {
        Order order = mapOrder(orderRequestDTO);
        User user = userDetailsRepository.findUserByUserName(orderRequestDTO.getUser())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        order.setUser(user);
        List<Product> product = productRepository.findByProductName(orderRequestDTO.getProductName());
        order.setProductList(product);
        orderRepository.save(order);
        return "Success";
    }

    @Override
    public String deleteOrder(OrderRequestDTO orderRequestDTO) {
        return "";
    }

    @Override
    public OrderResponseDTO viewOrder() {
        return null;
    }

    @Override
    public OrderResponseDTO viewAllOrder() {
        return null;
    }

    protected Order mapOrder(OrderRequestDTO orderRequestDTO) {
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        order.setOrderQuantity(orderRequestDTO.getQuantity());
        return order;
    }
}
