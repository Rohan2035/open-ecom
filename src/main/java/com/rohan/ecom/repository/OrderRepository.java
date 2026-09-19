package com.openecom.ecom.repository;

import com.openecom.ecom.entity.Order;
import org.hibernate.query.spi.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
            SELECT o from Order o
            where o.userId = :userId
            ORDER by o.orderDate DESC
            """)
    Optional<List<Order>> getProducts(@Param("userId") Integer username, Limit limit);

}
