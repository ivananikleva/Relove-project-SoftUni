package com.relove.integration;

import com.relove.controller.HomeController;
import com.relove.service.ProductService;
import com.relove.model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class HomeControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        // Настройваме MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(productService)).build();
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testIndex() throws Exception {
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(100.0);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(200.0);

        List<Product> featuredProducts = Arrays.asList(product1, product2);

        when(productService.getFeaturedProducts()).thenReturn(featuredProducts);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("featuredProducts", featuredProducts));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testProducts() throws Exception {

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-products"));
    }

}
