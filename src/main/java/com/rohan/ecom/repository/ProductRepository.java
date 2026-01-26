package com.rohan.ecom.repository;

import com.rohan.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductName(String productName);

    @Modifying
    @Query("""
            UPDATE Product p
            SET p.productQuantity = p.productQuantity - :quantity,
            p.reservedQuantity = p.reservedQuantity + :quantity
            WHERE p.productId = :id
            AND p.productQuantity >= :quantity
            """)
    int reserveProductQuantity(@Param("id") Integer productId, @Param("quantity") Integer productQuantity);

    @Modifying
    @Query("""
            UPDATE Product p
            SET p.productQuantity = p.productQuantity + :quantity,
            p.reservedQuantity = p.reservedQuantity - :quantity
            WHERE p.productId = :id
            AND p.productQuantity >= :quantity
            """)
    int releaseReservedQuantities(@Param("id") Integer productId, @Param("quantity") Integer productQuantity);

    @Modifying
    @Query("""
            UPDATE Product p
            SET p.reservedQuantity = p.reservedQuantity - :quantity
            WHERE p.productId = :id
            """)
    int confirmOrder(@Param("id") Integer productId, @Param("quantity") Integer productQuantity);

    Optional<List<Product>> findByProductNameIn(Set<String> productName);
}
