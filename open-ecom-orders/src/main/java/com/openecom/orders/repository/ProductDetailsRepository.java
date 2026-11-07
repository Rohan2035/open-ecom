package com.openecom.orders.repository;

import com.openecom.orders.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {

    Optional<List<ProductDetails>> findByProductNameIn(List<String> productNames);
}
