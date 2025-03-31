package com.relove.cart.controller;

import com.relove.cart.model.CartItem;
import com.relove.cart.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemService service;

    public CartController(CartItemService service) {
        this.service = service;
    }

    @GetMapping("/{email}")
    public List<CartItem> getCart(@PathVariable String email) {
        return service.getItemsForUser(email);
    }

    @PostMapping
    public CartItem addToCart(@RequestBody CartItem item) {
        return service.addItem(item);
    }

    @DeleteMapping("/{email}/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable String email,
            @PathVariable Long productId) {

        service.removeByUserAndProduct(email, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> clearCart(@PathVariable String email) {
        service.clearCartForUser(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateQuantity(@RequestBody CartItem item) {
        service.updateQuantity(item.getUserEmail(), item.getProductId(), item.getQuantity());
        return ResponseEntity.noContent().build();
    }



}




