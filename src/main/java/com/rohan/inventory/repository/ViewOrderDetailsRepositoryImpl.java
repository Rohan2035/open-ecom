package com.rohan.inventory.repository;

import com.rohan.inventory.entity.ViewOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ViewOrderDetailsRepositoryImpl implements ViewOrderDetailsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<List<ViewOrder>> findOrders(String userEmail, LocalDate orderDate, String orderCode) {
        String jpqlQuery = """
                    SELECT o FROM ViewOrder o
                    WHERE o.user.email = :userEmail
                    OR o.orderCode = :orderCode
                    OR o.orderDate = :orderDate
                """;

        Query query = entityManager.createQuery(jpqlQuery, ViewOrder.class);
        query.setMaxResults(10);
        query.setParameter("userEmail", userEmail);
        query.setParameter("orderCode", orderCode);
        query.setParameter("orderDate", orderDate);

        @SuppressWarnings("unchecked")
        List<ViewOrder> orders = query.getResultList();

        return Optional.of(orders);
    }
}
