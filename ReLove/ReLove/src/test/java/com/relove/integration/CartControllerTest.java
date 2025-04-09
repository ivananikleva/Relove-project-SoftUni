package com.relove.integration;

import com.relove.controller.CartController;
import com.relove.model.entity.UserEntity;
import com.relove.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CartControllerTest {

    @Autowired
    private CartService cartService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();

            UserEntity user = new UserEntity("testuser@example.com", "password");
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(user, "password"));
            SecurityContextHolder.setContext(securityContext);

    }

    @Test
    @WithMockUser(username = "testuser@example.com") // Мокваме потребител
    public void testAddToCart() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();

        mockMvc.perform(get("/add-to-cart/1"))  // 1 е примерен ID на продукт
                .andExpect(status().is3xxRedirection())  // Очакваме пренасочване след успешното добавяне
                .andExpect(view().name("redirect:/products/details/1"));  // Пренасочване към детайли за продукта
    }

    @Test
    @WithMockUser(username = "testuser@example.com") // Мокваме потребител
    public void testRemoveFromCart() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();

        mockMvc.perform(post("/remove-from-cart/1"))  // 1 е примерен ID на продукт
                .andExpect(status().is3xxRedirection())  // Очакваме пренасочване
                .andExpect(view().name("redirect:/cart"));  // Пренасочване към страницата на количката
    }

    @Test
    @WithMockUser(username = "testuser@example.com") // Мокваме потребител
    public void testViewCart() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"));  // Очакваме изгледа на страницата "cart"
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testUpdateCartQuantity() throws Exception {
        Long productId = 1L;
        int newQuantity = 2;

        String jsonContent = "{\"productId\": " + productId + ", \"quantity\": " + newQuantity + ", \"userEmail\": \"testuser@example.com\"}";

        mockMvc.perform(put("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNoContent());
    }
}
