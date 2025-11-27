package com.rohan.inventory.repository;

import com.rohan.inventory.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.user.userName = :username")
    Optional<Order> findByUserName(@Param("username") String username);
}
