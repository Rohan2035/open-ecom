package com.rohan.ecom.controller;


import com.rohan.ecom.dto.OrderRequestDTO;
import com.rohan.ecom.dto.ViewOrderResponseDTO;
import com.rohan.ecom.dto.ViewOrderRequestDTO;
import com.rohan.ecom.service.OrderService;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/getorder")
    public ViewOrderResponseDTO getOrder(@RequestBody ViewOrderRequestDTO requestDTO) {
        LOG.info("Fetching Order");
        return orderService.viewOrder(requestDTO);
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
