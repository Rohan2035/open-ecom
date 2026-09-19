package com.openecom.ecom.repository;

import com.openecom.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductName(String productName);

    Optional<List<Product>> findByProductNameIn(Set<String> productName);

    Optional<List<Product>> findTop20By();
}
