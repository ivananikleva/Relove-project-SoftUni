package com.relove.cart;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.relove.cart.model.CartItem;
import com.relove.cart.repository.CartItemRepo;
import com.relove.cart.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class CartItemServiceUnitTest {

    @Mock
    private CartItemRepo cartItemRepo;

    private CartItemService cartItemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cartItemService = new CartItemService(cartItemRepo);
    }

    @Test
    public void testGetItemsForUser() {
        CartItem cartItem = new CartItem();
        cartItem.setUserEmail("test@example.com");
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        when(cartItemRepo.findAllByUserEmail("test@example.com")).thenReturn(List.of(cartItem));

        List<CartItem> result = cartItemService.getItemsForUser("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getUserEmail());
        assertEquals(1L, result.get(0).getProductId());
    }

    @Test
    public void testAddItemWhenItemExists() {
        CartItem cartItem = new CartItem();
        cartItem.setUserEmail("test@example.com");
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        CartItem existingItem = new CartItem();
        existingItem.setUserEmail("test@example.com");
        existingItem.setProductId(1L);
        existingItem.setQuantity(3);

        when(cartItemRepo.findByUserEmailAndProductId("test@example.com", 1L)).thenReturn(Optional.of(existingItem));
        when(cartItemRepo.save(existingItem)).thenReturn(existingItem);

        CartItem result = cartItemService.addItem(cartItem);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        verify(cartItemRepo).save(existingItem);
    }

    @Test
    public void testAddItemWhenItemDoesNotExist() {
        CartItem cartItem = new CartItem();
        cartItem.setUserEmail("test@example.com");
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        when(cartItemRepo.findByUserEmailAndProductId("test@example.com", 1L)).thenReturn(Optional.empty());
        when(cartItemRepo.save(cartItem)).thenReturn(cartItem);

        CartItem result = cartItemService.addItem(cartItem);

        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        verify(cartItemRepo).save(cartItem);
    }

    @Test
    public void testRemoveByUserAndProduct() {
        // Стимулиране на deleteByUserEmailAndProductId
        doNothing().when(cartItemRepo).deleteByUserEmailAndProductId("test@example.com", 1L);

        cartItemService.removeByUserAndProduct("test@example.com", 1L);

        // Проверка дали методът е извикан
        verify(cartItemRepo, times(1)).deleteByUserEmailAndProductId("test@example.com", 1L);
    }

    @Test
    public void testUpdateQuantity() {
        CartItem cartItem = new CartItem();
        cartItem.setUserEmail("test@example.com");
        cartItem.setProductId(1L);
        cartItem.setQuantity(3);

        when(cartItemRepo.findByUserEmailAndProductId("test@example.com", 1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepo.save(cartItem)).thenReturn(cartItem);

        cartItemService.updateQuantity("test@example.com", 1L, 5);

        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepo).save(cartItem);
    }

    @Test
    public void testUpdateQuantityWhenItemNotFound() {
        when(cartItemRepo.findByUserEmailAndProductId("test@example.com", 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            cartItemService.updateQuantity("test@example.com", 1L, 5);
        });
    }

    @Test
    public void testClearCartForUser() {
        doNothing().when(cartItemRepo).deleteAllByUserEmail("test@example.com");

        cartItemService.clearCartForUser("test@example.com");

        verify(cartItemRepo, times(1)).deleteAllByUserEmail("test@example.com");
    }
}
