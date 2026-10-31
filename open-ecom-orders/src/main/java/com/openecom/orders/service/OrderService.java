package com.openecom.orders.service;

import com.openecom.orders.dto.OrderRequestDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderService {

    public Map<String, String> createOrder(OrderRequestDTO orderRequestDTO) {

        return Map.of("status", "order successfully created");
    }



}
