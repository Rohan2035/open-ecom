package com.rohan.inventory.repository;

import com.rohan.inventory.dto.OrderNativeSqlResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ViewOrderDetailsRepository  {

    List<OrderNativeSqlResponseDTO> fetchOrders(String userEmail, LocalDate orderDate, String orderCode);
}
