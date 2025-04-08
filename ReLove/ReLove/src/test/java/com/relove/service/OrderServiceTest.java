package com.relove.service;

import com.relove.client.CartClient;
import com.relove.model.dto.CartItemDTO;
import com.relove.model.entity.Order;
import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.repo.OrderRepo;
import com.relove.repo.ProductRepo;
import com.relove.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CartClient cartClient;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrderSuccessfully() {
        String email = "test@relove.bg";

        UserEntity buyer = new UserEntity();
        buyer.setEmail(email);

        Product product = new Product();
        product.setId(1L);
        product.setName("Продукт");

        CartItemDTO item = new CartItemDTO();
        item.setProductId(1L);
        item.setQuantity(2);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(buyer));
        when(cartClient.getCart(email)).thenReturn(List.of(item));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        Order savedOrder = new Order();
        savedOrder.setBuyer(buyer);
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(email);

        assertEquals(buyer, result.getBuyer());
        assertEquals(1, result.getItems().size());
        assertEquals(product, result.getItems().get(0).getProduct());
        assertEquals(2, result.getItems().get(0).getQuantity());

        verify(cartClient).clearCart(email);
        verify(orderRepo).save(any(Order.class));
    }

    @Test
    void testCreateOrderFailsWhenCartIsEmpty() {
        String email = "user@example.com";
        UserEntity buyer = new UserEntity();
        buyer.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(buyer));
        when(cartClient.getCart(email)).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.createOrder(email));
        assertEquals("Количката е празна.", ex.getMessage());
    }

    @Test
    void testGetOrdersByUserReturnsOrders() {
        String email = "test@relove.bg";
        Order order1 = new Order();
        Order order2 = new Order();

        when(orderRepo.findAllByBuyerEmailWithItemsAndProducts(email))
                .thenReturn(List.of(order1, order2));

        List<Order> result = orderService.getOrdersByUser(email);

        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
        verify(orderRepo).findAllByBuyerEmailWithItemsAndProducts(email);
    }

    @Test
    void testGetAllOrdersReturnsAllOrders() {
        Order order1 = new Order();
        Order order2 = new Order();

        when(orderRepo.findAllWithItemsAndBuyer())
                .thenReturn(List.of(order1, order2));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertTrue(result.contains(order1));
        assertTrue(result.contains(order2));
        verify(orderRepo).findAllWithItemsAndBuyer();
    }

}

