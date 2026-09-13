package com.openecom.orders.service;

import com.openecom.orders.dao.OrderDao;
import com.openecom.orders.dto.OrderRequestDTO;
import com.openecom.orders.dto.ViewOrderRequestDTO;
import com.openecom.orders.dto.ViewOrderResponseDTO;
import com.openecom.orders.entity.Order;
import com.openecom.orders.repository.OrderRepository;
import com.openecom.orders.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao,
                            OrderRepository orderRepository,
                            ProductRepository productRepository) {

        this.orderDao = orderDao;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Map<String, String> createOrder(OrderRequestDTO orderRequestDTO) {
        Map<String, String> response = new HashMap<>();

        return response;
    }

    @Override
    public ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO) {
        return null;
    }
}
