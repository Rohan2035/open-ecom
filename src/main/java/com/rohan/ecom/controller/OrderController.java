package com.openecom.ecom.controller;


import com.openecom.ecom.dto.OrderRequestDTO;
import com.openecom.ecom.dto.ViewOrderRequestDTO;
import com.openecom.ecom.dto.ViewOrderResponseDTO;
import com.openecom.ecom.service.OrderService;
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
        LOG.info("=== Fetching Orders ====");
        long startTime =  System.currentTimeMillis();

        responseDTO =  orderService.viewOrder(requestDTO);

        long endTime = System.currentTimeMillis();
        LOG.info("Get Orders - Total Time Taken: {} ms", (endTime - startTime));

        return responseDTO;
    }

    @PostMapping("/addorder")
    public Map<String, String> addOrder(@RequestBody OrderRequestDTO requestDTO) {
        LOG.info("=== Adding Orders ===");
        long startTime = System.currentTimeMillis();

        Map<String, String> status =  orderService.createOrder(requestDTO);

        long endTime = System.currentTimeMillis();
        LOG.info("Add Order - Total Time Taken: {} ms", (endTime - startTime));

        LOG.info("<============ Order status: {} =============>", status);
        return status;
    }
}
