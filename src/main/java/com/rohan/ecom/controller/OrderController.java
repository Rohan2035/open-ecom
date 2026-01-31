package com.rohan.ecom.controller;


import com.rohan.ecom.dto.OrderRequestDTO;
import com.rohan.ecom.dto.ViewOrderRequestDTO;
import com.rohan.ecom.dto.ViewOrderResponseDTO;
import com.rohan.ecom.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/getorder")
    public ViewOrderResponseDTO getOrder(@RequestBody ViewOrderRequestDTO requestDTO) {
        ViewOrderResponseDTO responseDTO;
        LOG.info("<====== Fetching Orders ============>");
        long startTime =  System.currentTimeMillis();

        responseDTO =  orderService.viewOrder(requestDTO);

        long endTime = System.currentTimeMillis();
        LOG.info("Get Order - Total Time Taken: {} ms", (endTime - startTime));

        return responseDTO;
    }

    @PostMapping("/addorder")
    public Map<String, String> addOrder(@RequestBody OrderRequestDTO requestDTO) {
        LOG.info("<== Adding Orders ==>");
        long startTime = System.currentTimeMillis();

        String status =  orderService.addOrder(requestDTO);

        long endTime = System.currentTimeMillis();
        LOG.info("Add Order - Total Time Taken: {} ms", (endTime - startTime));

        LOG.info("<============ Order status: {} =============>", status);
        return Map.of("Status", status);
    }
}
