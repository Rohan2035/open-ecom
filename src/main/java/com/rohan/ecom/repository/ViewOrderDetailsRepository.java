package com.openecom.ecom.repository;

import com.openecom.ecom.dto.OrderNativeSqlResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ViewOrderDetailsRepository  {

    List<OrderNativeSqlResponseDTO> fetchOrders(String userEmail, LocalDate orderDate, String orderCode);
}
