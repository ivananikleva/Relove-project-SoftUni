package com.relove.service;

import com.relove.client.CartClient;
import com.relove.model.dto.CartItemDTO;
import com.relove.model.entity.Product;
import com.relove.repo.ProductRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartClient cartClient;

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private CartService cartService;

    @Test
    void testGetCartItemsPopulatesProductDetails() {
        String email = "user@relove.bg";
        Long productId = 1L;

        CartItemDTO item = new CartItemDTO();
        item.setProductId(productId);
        item.setQuantity(2);

        Product product = new Product();
        product.setId(productId);
        product.setName("Еко чанта");
        product.setPrice(25.50);
        product.setImageUrl("/uploads/bag.jpg");

        when(cartClient.getCart(email)).thenReturn(List.of(item));
        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        List<CartItemDTO> result = cartService.getCartItems(email);

        assertEquals(1, result.size());
        CartItemDTO resultItem = result.get(0);

        assertEquals("Еко чанта", resultItem.getProductName());
        assertEquals("/uploads/bag.jpg", resultItem.getImageUrl());
        assertEquals(new BigDecimal("25.50"), resultItem.getPrice());
    }

    @Test
    void testAddToCartDelegatesToClient() {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(1L);
        dto.setQuantity(1);

        cartService.addToCart(dto);

        verify(cartClient).addToCart(dto);
    }

    @Test
    void testRemoveFromCartDelegatesToClient() {
        String email = "test@relove.bg";
        Long productId = 2L;

        cartService.removeFromCart(email, productId);

        verify(cartClient).removeFromCart(email, productId);
    }

    @Test
    void testUpdateQuantityDelegatesToClient() {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(1L);
        dto.setQuantity(3);

        cartService.updateQuantity(dto);

        verify(cartClient).updateQuantity(dto);
    }
}
