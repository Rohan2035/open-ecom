package com.rohan.inventory.service;

import com.rohan.inventory.DTO.OrderRequestDTO;
import com.rohan.inventory.DTO.OrderResponseDTO;

public interface OrderService {
    String addOrder(OrderRequestDTO orderRequestDTO);
    String deleteOrder(OrderRequestDTO orderRequestDTO);
    OrderResponseDTO viewOrder();
    OrderResponseDTO viewAllOrder();
}
