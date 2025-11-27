package com.rohan.inventory.repository;

import com.rohan.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByUserName(String username);
}
