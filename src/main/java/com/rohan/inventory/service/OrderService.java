package com.rohan.inventory.service;

import com.rohan.inventory.dto.OrderRequestDTO;
import com.rohan.inventory.dto.OrderResponseDTO;
import com.rohan.inventory.dto.ViewOrderRequestDTO;

import java.util.List;

public interface OrderService {
    String addOrder(OrderRequestDTO orderRequestDTO);
    String deleteOrder(OrderRequestDTO orderRequestDTO);
    List<OrderResponseDTO> viewOrder(ViewOrderRequestDTO orderRequestDTO);
    List<OrderResponseDTO> viewAllOrder(String username);
}
