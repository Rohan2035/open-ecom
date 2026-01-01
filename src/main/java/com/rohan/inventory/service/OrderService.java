package com.rohan.inventory.service;

import com.rohan.inventory.dto.OrderRequestDTO;
import com.rohan.inventory.dto.ViewOrderResponseDTO;
import com.rohan.inventory.dto.ViewOrderRequestDTO;

import java.util.List;

public interface OrderService {
    String addOrder(OrderRequestDTO orderRequestDTO);
    String deleteOrder(OrderRequestDTO orderRequestDTO);
    ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO);
    List<ViewOrderResponseDTO> viewAllOrder(String username);
}
