package com.relove.cart.service;

import com.relove.cart.model.CartItem;
import com.relove.cart.repository.CartItemRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    private final CartItemRepo repo;

    public CartItemService(CartItemRepo repo) {
        this.repo = repo;
    }


    public List<CartItem> getItemsForUser(String email) {
        return repo.findAllByUserEmail(email);
    }

    public CartItem addItem(CartItem item) {
        Optional<CartItem> existingItemOpt = repo.findByUserEmailAndProductId(item.getUserEmail(), item.getProductId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            return repo.save(existingItem);
        }

        return repo.save(item);
        }


    @Transactional
    public void removeByUserAndProduct(String email, Long productId) {
        repo.deleteByUserEmailAndProductId(email, productId);
    }

    @Transactional
    public void updateQuantity(String email, Long productId, int quantity) {
        CartItem item = repo.findByUserEmailAndProductId(email, productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(quantity);
        repo.save(item);
    }

    @Transactional
    public void clearCartForUser(String email) {
        repo.deleteAllByUserEmail(email);
    }


}
