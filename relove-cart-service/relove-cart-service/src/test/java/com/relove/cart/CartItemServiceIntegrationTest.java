package com.relove.cart;
import static org.junit.jupiter.api.Assertions.*;

import com.relove.cart.model.CartItem;
import com.relove.cart.repository.CartItemRepo;
import com.relove.cart.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class CartItemServiceIntegrationTest {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartItemRepo cartItemRepo;

    private CartItem cartItem;

    @BeforeEach
    public void setUp() {
        cartItem = new CartItem();
        cartItem.setUserEmail("testuser@example.com");
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        cartItemRepo.save(cartItem);
    }

    @Test
    public void testGetItemsForUser() {
        List<CartItem> items = cartItemService.getItemsForUser("testuser@example.com");

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals("testuser@example.com", items.get(0).getUserEmail());
        assertEquals(2, items.get(0).getQuantity());
    }

    @Test
    public void testAddItem() {
        CartItem newItem = new CartItem();
        newItem.setUserEmail("testuser@example.com");
        newItem.setProductId(2L);
        newItem.setQuantity(3);

        CartItem savedItem = cartItemService.addItem(newItem);

        assertNotNull(savedItem);
        assertEquals(3, savedItem.getQuantity());
        assertEquals("testuser@example.com", savedItem.getUserEmail());
    }



    @Test
    public void testRemoveByUserAndProduct() {
        cartItemService.removeByUserAndProduct("testuser@example.com", 1L);

        List<CartItem> items = cartItemService.getItemsForUser("testuser@example.com");
        assertTrue(items.isEmpty()); // Количката трябва да е празна
    }

    @Test
    public void testUpdateQuantity() {
        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setUserEmail("testuser@example.com");
        updatedCartItem.setProductId(1L);
        updatedCartItem.setQuantity(10);

        cartItemService.updateQuantity("testuser@example.com", 1L, 10);

        List<CartItem> items = cartItemService.getItemsForUser("testuser@example.com");

        assertEquals(10, items.get(0).getQuantity());
    }

    @Test
    public void testClearCartForUser() {
        cartItemService.clearCartForUser("testuser@example.com");

        List<CartItem> items = cartItemService.getItemsForUser("testuser@example.com");

        assertTrue(items.isEmpty());
    }
}
