package com.rohan.inventory.repository;

import com.rohan.inventory.entity.ViewOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ViewOrderDetailsRepository  {

    Optional<List<ViewOrder>> findOrders(String userEmail, LocalDate orderDate,
        String orderCode);
}
