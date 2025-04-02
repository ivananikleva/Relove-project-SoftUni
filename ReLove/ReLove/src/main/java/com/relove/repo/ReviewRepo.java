package com.relove.repo;

import com.relove.model.entity.Product;
import com.relove.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
}
