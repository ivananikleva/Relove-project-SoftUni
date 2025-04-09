package com.relove.integration;

import com.relove.controller.UserController;
import com.relove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    public void testShowRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "New User")
                        .param("email", "newuser@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testViewProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testEditProfileForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testUpdateProfile() throws Exception {
        mockMvc.perform(post("/profile/edit")
                        .param("name", "Updated Name")
                        .param("email", "testuser@example.com")
                        .param("password", "newpassword123")
                        .param("confirmPassword", "newpassword123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testAddToFavorites() throws Exception {
        mockMvc.perform(get("/add-to-favorites/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/details/1"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testShowFavorites() throws Exception {
        mockMvc.perform(get("/favorites"))
                .andExpect(status().isOk())
                .andExpect(view().name("favorites"))
                .andExpect(model().attributeExists("favoriteProducts"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testRemoveFromFavorites() throws Exception {
        mockMvc.perform(get("/remove-favorite/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/favorites"));
    }
}
