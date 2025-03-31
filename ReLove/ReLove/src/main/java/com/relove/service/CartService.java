package com.relove.service;

import com.relove.client.CartClient;
import com.relove.model.dto.CartItemDTO;
import com.relove.model.entity.Product;
import com.relove.repo.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartClient cartClient;
    private final ProductRepo productRepo;

    public CartService(CartClient cartClient, ProductRepo productRepo) {
        this.cartClient = cartClient;
        this.productRepo = productRepo;
    }

    public List<CartItemDTO> getCartItems(String userEmail) {
        List<CartItemDTO> baseItems = cartClient.getCart(userEmail);

        return baseItems.stream().map(item -> {
            Product product = productRepo.findById(item.getProductId()).orElse(null);
            if (product != null) {
                item.setProductName(product.getName());
                item.setImageUrl(product.getImageUrl());
                item.setPrice(product.getPrice());
            }
            return item;
        }).toList();
    }

    public void addToCart(CartItemDTO item) {
        cartClient.addToCart(item);
    }

    public void removeFromCart(String email, Long productId) {
        cartClient.removeFromCart(email, productId);
    }

    public void updateQuantity(CartItemDTO dto) {
        cartClient.updateQuantity(dto); // извиква микросървиса
    }


}
