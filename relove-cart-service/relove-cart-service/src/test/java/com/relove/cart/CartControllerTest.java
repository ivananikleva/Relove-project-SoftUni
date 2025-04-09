
package com.relove.cart;
import com.relove.cart.controller.CartController;
import com.relove.cart.model.CartItem;
import com.relove.cart.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }


    @Test
    public void testUpdateQuantity() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setUserEmail("testuser@example.com");
        cartItem.setProductId(1L);
        cartItem.setQuantity(3);

        doNothing().when(cartItemService).updateQuantity("testuser@example.com", 1L, 3);

        mockMvc.perform(put("/api/cart")
                        .contentType("application/json")
                        .content("{\"userEmail\": \"testuser@example.com\", \"productId\": 1, \"quantity\": 3}"))
                .andExpect(status().isNoContent());

        verify(cartItemService, times(1)).updateQuantity("testuser@example.com", 1L, 3);
    }

    @Test
    public void testRemoveItemFromCart() throws Exception {
        doNothing().when(cartItemService).removeByUserAndProduct("testuser@example.com", 1L);

        mockMvc.perform(delete("/api/cart/{email}/{productId}", "testuser@example.com", 1L))
                .andExpect(status().isNoContent());

        verify(cartItemService, times(1)).removeByUserAndProduct("testuser@example.com", 1L);
    }

    @Test
    public void testClearCart() throws Exception {
        doNothing().when(cartItemService).clearCartForUser("testuser@example.com");

        mockMvc.perform(delete("/api/cart/{email}", "testuser@example.com"))
                .andExpect(status().isNoContent());

        verify(cartItemService, times(1)).clearCartForUser("testuser@example.com");
    }
}
