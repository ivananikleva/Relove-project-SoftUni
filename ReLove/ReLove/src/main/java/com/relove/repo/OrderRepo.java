package com.relove.repo;

import com.relove.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.buyer.email = :email " +
            "ORDER BY o.createdAt DESC")
    List<Order> findAllByBuyerEmailWithItemsAndProducts(@Param("email") String email);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "LEFT JOIN FETCH o.buyer " +
            "ORDER BY o.createdAt DESC")
    List<Order> findAllWithItemsAndBuyer();

}
