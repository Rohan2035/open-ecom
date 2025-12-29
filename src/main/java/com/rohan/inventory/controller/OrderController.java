package com.rohan.inventory.controller;


import com.rohan.inventory.dto.OrderRequestDTO;
import com.rohan.inventory.dto.OrderResponseDTO;
import com.rohan.inventory.dto.ViewOrderRequestDTO;
import com.rohan.inventory.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
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
    public List<OrderResponseDTO> getOrder(@RequestBody ViewOrderRequestDTO requestDTO) {
        return null;
    }

    @PostMapping("/addorder")
    public Map<String, String> addOrder(@RequestBody OrderRequestDTO requestDTO) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOG.info("<== Adding Orders ==>");
        String status =  orderService.addOrder(requestDTO);
        stopWatch.stop();
        LOG.info("Time Taken: {} ms", stopWatch.getTotalTimeMillis());
        LOG.info("Order status: {}", status);
        return Map.of("Status", status);
    }

    @DeleteMapping
    public Map<String, String> deleteOrder(@RequestParam Integer orderId) {
        return null;
    }
}
