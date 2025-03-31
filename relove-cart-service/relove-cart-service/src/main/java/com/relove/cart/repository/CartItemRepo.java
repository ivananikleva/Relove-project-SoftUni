package com.relove.cart.repository;

import com.relove.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserEmail(String email);

    void deleteAllByUserEmail(String email);

    void deleteByUserEmailAndProductId(String email, Long productId);

    Optional<CartItem> findByUserEmailAndProductId(String userEmail, Long productId);
}
