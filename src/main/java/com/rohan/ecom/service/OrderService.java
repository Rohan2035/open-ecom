package com.openecom.ecom.service;

import com.openecom.ecom.dto.OrderRequestDTO;
import com.openecom.ecom.dto.ViewOrderResponseDTO;
import com.openecom.ecom.dto.ViewOrderRequestDTO;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Map<String, String> createOrder(OrderRequestDTO orderRequestDTO);
    ViewOrderResponseDTO viewOrder(ViewOrderRequestDTO orderRequestDTO);
}
