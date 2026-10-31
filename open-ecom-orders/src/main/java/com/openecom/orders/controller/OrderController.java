package com.openecom.orders.controller;

import com.openecom.orders.dto.OrderRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @PostMapping("/addorder")
    public Map<String, String> addOrder(OrderRequestDTO orderRequestDTO) {

        return Map.of("Status", "Success");
    }
}
