package com.rohan.inventory.controller;


import com.rohan.inventory.DTO.OrderRequestDTO;
import com.rohan.inventory.DTO.OrderResponseDTO;
import com.rohan.inventory.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/getorder")
    public List<OrderResponseDTO> getOrder(@RequestBody OrderRequestDTO requestDTO) {
        return null;
    }

    @PostMapping("/addorder")
    public Map<String, String> addOrder(@RequestBody OrderRequestDTO requestDTO) {
        return null;
    }

    @DeleteMapping
    public Map<String, String> deleteOrder(@RequestParam Integer orderId) {
        return null;
    }
}
