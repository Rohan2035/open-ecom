package com.openecom.ecom.repository;

import com.openecom.ecom.entity.Quantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuantityRepository extends JpaRepository<Quantity, Long> {

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Quantity q
        set q.reservedQuantity = q.reservedQuantity + :quantity,
        q.productQuantity = q.productQuantity - :quantity
        WHERE q.productId = :id
        AND q.productQuantity >= :quantity
    """)
    int reserveProductQuantity(@Param("id") Long id, @Param("quantity") Integer productQuantity);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Quantity q
        set q.reservedQuantity = q.reservedQuantity - :quantity
        WHERE q.productId = :id
        AND q.reservedQuantity >= :quantity
    """)
    int confirmProductQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Quantity q
        set q.productQuantity = q.productQuantity + :quantity,
        q.reservedQuantity = q.reservedQuantity - :quantity
        WHERE q.productId = :id
        AND q.productQuantity >= :quantity
        AND q.reservedQuantity >= :quantity
    """)
    void releaseProductQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Quantity q
        set q.productQuantity = q.productQuantity + :quantity
        where q.productId = :id
    """)
    void rollbackQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
}
