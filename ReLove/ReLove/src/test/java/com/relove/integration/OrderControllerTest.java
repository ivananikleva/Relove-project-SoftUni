package com.relove.integration;

import com.relove.controller.OrderController;
import com.relove.model.entity.Order;
import com.relove.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testCreateOrder() throws Exception {
        // Създаване на поръчка за потребителя
        mockMvc.perform(post("/orders/create"))
                .andExpect(status().is3xxRedirection()) // Проверка за редирект
                .andExpect(view().name("order-confirmation")) // Проверка за правилната страница
                .andExpect(model().attributeExists("orderDate")); // Проверка за съществуване на атрибут
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testMyOrders() throws Exception {
        // Мокване на поръчки за потребителя
        Order order1 = new Order();
        Order order2 = new Order();

        // Симулираме връщането на поръчки от OrderService
        mockMvc.perform(get("/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-orders")) // Проверка на правилната страница
                .andExpect(model().attributeExists("orders")); // Проверка дали атрибутът "orders" е наличен в модела
    }
}
