package com.relove.integration;


import com.relove.client.CartClient;
import com.relove.model.dto.CartItemDTO;
import com.relove.model.entity.Product;
import com.relove.repo.ProductRepo;
import com.relove.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;  // Реален CartService

    @Autowired
    private ProductRepo productRepo;  // Реално ProductRepo

    @Autowired
    private CartClient cartClient;  // Реален CartClient

    private Product testProduct;

    @BeforeEach
    public void setUp() {
        testProduct = new Product();
        testProduct.setName("Test Product 1");
        testProduct.setPrice(100.0);
        testProduct.setImageUrl("http://example.com/image.jpg");
        productRepo.save(testProduct);
    }

    @Test
    public void testGetCartItems() {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(testProduct.getId());
        cartItemDTO.setQuantity(2);

        // Добавяме продукта към количката чрез реалния CartClient
        cartClient.addToCart(cartItemDTO);

        // Проверяваме дали количката е актуализирана
        List<CartItemDTO> cartItems = cartService.getCartItems("testuser@example.com");

        assertEquals(1, cartItems.size(), "Трябва да има един продукт в количката.");
        assertEquals("Test Product 1", cartItems.get(0).getProductName(), "Името на продукта не съвпада.");
        assertEquals(100.0, cartItems.get(0).getPrice(), "Цената на продукта не съвпада.");
        assertEquals("http://example.com/image.jpg", cartItems.get(0).getImageUrl(), "URL на изображението не съвпада.");
    }

    @Test
    public void testAddToCart() {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(testProduct.getId());
        cartItemDTO.setQuantity(2);

        // Добавяме продукта в количката чрез реалния CartClient
        cartService.addToCart(cartItemDTO);

        // Проверяваме дали продуктът е добавен към количката
        List<CartItemDTO> cartItems = cartClient.getCart("testuser@example.com");
        assertTrue(cartItems.stream().anyMatch(item -> item.getProductId().equals(testProduct.getId())), "Продуктът не е добавен към количката.");
    }

    @Test
    public void testRemoveFromCart() {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(testProduct.getId());
        cartItemDTO.setQuantity(2);

        cartService.addToCart(cartItemDTO);

        // Премахваме продукта от количката чрез реалния CartClient
        cartService.removeFromCart("testuser@example.com", testProduct.getId());

        List<CartItemDTO> cartItems = cartClient.getCart("testuser@example.com");
        assertFalse(cartItems.stream().anyMatch(item -> item.getProductId().equals(testProduct.getId())), "Продуктът не е премахнат от количката.");
    }

    @Test
    public void testUpdateQuantity() {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(testProduct.getId());
        cartItemDTO.setQuantity(2);

        cartService.addToCart(cartItemDTO);

        // Актуализираме количеството на продукта
        cartItemDTO.setQuantity(3);
        cartService.updateQuantity(cartItemDTO);

        List<CartItemDTO> cartItems = cartClient.getCart("testuser@example.com");
        assertTrue(cartItems.stream().anyMatch(item -> item.getProductId().equals(testProduct.getId()) && item.getQuantity() == 3),
                "Количеството на продукта не е обновено.");
    }
}
