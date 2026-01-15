package com.rohan.ecom.service;

import com.rohan.ecom.dto.OrderRequestDTO;
import com.rohan.ecom.dto.ViewOrderResponseDTO;
import com.rohan.ecom.dto.ViewOrderRequestDTO;

import java.util.List;

public interface OrderService {
    String addOrder(OrderRequestDTO orderRequestDTO);
    String deleteOrder(OrderRequestDTO orderRequestDTO);
    ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO);
    List<ViewOrderResponseDTO> viewAllOrder(String username);
}
