package com.relove.repo;

import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findAllBySeller(UserEntity seller);

    List<Product> findTop6ByOrderByIdDesc();
}
