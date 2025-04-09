package com.relove.integration;

import com.relove.client.CartClient;
import com.relove.model.dto.CartItemDTO;
import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.Order;
import com.relove.model.entity.Product;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.OrderRepo;
import com.relove.repo.ProductRepo;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import com.relove.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    @MockBean
    private CartClient cartClient;

    private UserEntity testUser;
    private Product testProduct;

    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    public void setUp() {
        String userEmail = "testuser@example.com";
        if (userRepo.findByEmail(userEmail).isEmpty()) {
            testUser = new UserEntity();
            testUser.setName("Test User");
            testUser.setEmail(userEmail);
            testUser.setPassword(new BCryptPasswordEncoder().encode("password123"));

            RoleEntity userRole = roleRepo.findByRole(UserRoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            testUser.setRoles(Set.of(userRole));
            userRepo.save(testUser);
        }else {
            testUser = userRepo.findByEmail(userEmail).get();
        }

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        productRepo.save(testProduct);

        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setProductId(testProduct.getId());
        cartItem.setQuantity(2);

        when(cartClient.getCart(testUser.getEmail())).thenReturn(List.of(cartItem));

        doNothing().when(cartClient).addToCart(cartItem);
    }

    @Test
    public void testCreateOrder() {
        Order order = orderService.createOrder(testUser.getEmail());

        assertNotNull(order);
        assertEquals(testUser.getEmail(), order.getBuyer().getEmail());
        assertEquals(1, order.getItems().size()); // Поръчката трябва да съдържа 1 продукт

    }

    @Test
    public void testGetOrdersByUser() {
        orderService.createOrder(testUser.getEmail());

        List<Order> orders = orderService.getOrdersByUser(testUser.getEmail());

        assertFalse(orders.isEmpty(), "Потребителят трябва да има поне една поръчка.");
        assertEquals(testUser.getEmail(), orders.get(0).getBuyer().getEmail());
    }

    @Test
    public void testGetAllOrders() {
        orderService.createOrder(testUser.getEmail());

        List<Order> orders = orderService.getAllOrders();

        assertFalse(orders.isEmpty(), "Трябва да има поне една поръчка.");
        assertEquals(testUser.getEmail(), orders.get(0).getBuyer().getEmail());
    }
}

