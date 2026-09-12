package com.openecom.orders.dao;

import com.openecom.orders.dto.OrderRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDao {
    public void reserveProductQuantities(Long id, Integer quantity){}

    public void confirmProductQuantity(List<OrderRequestDTO.InnerOrderRequestDTO> orderRequestDTOS){}

    public void releaseProductQuantity(List<OrderRequestDTO.InnerOrderRequestDTO> orderRequestDTOS){}

    public void rollbackQuantity(List<OrderRequestDTO.InnerOrderRequestDTO> requests){}
}
