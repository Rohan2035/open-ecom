package com.rohan.inventory.service;

import com.rohan.inventory.DTO.OrderRequestDTO;
import com.rohan.inventory.DTO.OrderResponseDTO;
import com.rohan.inventory.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public String addOrder(OrderRequestDTO orderRequestDTO) {
        return null;
    }

    @Override
    public String deleteOrder(OrderRequestDTO orderRequestDTO) {
        return "";
    }

    @Override
    public OrderResponseDTO viewOrder(String username) {
        return null;
    }

    @Override
    public List<OrderResponseDTO> viewAllOrder(String username) {
        return null;
    }

    protected Order mapOrder(OrderRequestDTO orderRequestDTO) {
        return null;
    }

    protected OrderResponseDTO mapOrderResponse(Order order) {
        return null;
    }
}
