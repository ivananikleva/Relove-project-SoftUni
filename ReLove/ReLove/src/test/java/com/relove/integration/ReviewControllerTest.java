package com.relove.integration;

import com.relove.controller.ReviewController;
import com.relove.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    private ReviewService reviewService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService)).build();
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testSubmitReview() throws Exception {
        mockMvc.perform(post("/reviews")
                        .param("productId", "1")
                        .param("rating", "5")
                        .param("comment", "Great product!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/details/1"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testDeleteReview() throws Exception {
        mockMvc.perform(post("/reviews/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/details/1"));
    }
}
