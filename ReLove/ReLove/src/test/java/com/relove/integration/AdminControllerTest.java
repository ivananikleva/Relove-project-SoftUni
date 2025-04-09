package com.relove.integration;

import com.relove.controller.AdminController;
import com.relove.service.OrderService;
import com.relove.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AdminControllerTest {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testViewAllUsers() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(userService, orderService)).build();

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testViewAllOrders() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(userService, orderService)).build();

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-orders"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testChangeUserRole() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/admin/users/change-role/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users"));
    }

}
