package com.relove.integration;

import com.relove.controller.ProductController;
import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import com.relove.service.ProductService;
import com.relove.service.ReviewService;
import com.relove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProductControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    private MockMvc mockMvc;
    private UserEntity testUser;

    @BeforeEach
    public void setUp() {
        if (userRepo.findByEmail("testuser@example.com").isEmpty()) {
            testUser = new UserEntity();
            testUser.setName("Test User");
            testUser.setEmail("testuser@example.com");
            testUser.setPassword("password123");

            RoleEntity userRole = roleRepo.findByRole(UserRoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            testUser.setRoles(Set.of(userRole));
            userRepo.save(testUser);
        } else {
            testUser = userRepo.findByEmail("testuser@example.com").orElseThrow();
        }

        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, userService, reviewService)).build();
    }

    @Test
    public void testShowAddForm() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/products/add").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("add-product"));
    }

    @Test
    public void testAddProduct() throws Exception {
        // Симулиране на сесия с тестовия потребител
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(post("/products/add")
                        .param("name", "New Product")
                        .param("price", "99.99")
                        .param("description", "Test description")
                        .param("imageUrl", "http://example.com/image.jpg")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/mine"));
    }

    @Test
    public void testShowUserProducts() throws Exception {
        // Симулиране на сесия с тестовия потребител
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/products/mine").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("my-products"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/products/delete/{id}", 1L).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/mine"));
    }

    @Test
    public void testShowAllProducts() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/products/all").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("all-products"));
    }

    @Test
    public void testShowProductDetails() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/products/details/{id}", 1L).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("reviews"));
    }

    @Test
    public void testShowEditForm() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(get("/products/edit/{id}", 1L).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-product"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Симулиране на сесия с тестовия потребител
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());

        User principal = new User(testUser.getEmail(), testUser.getPassword(),
                testUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet()));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        mockMvc.perform(post("/products/edit/{id}", 1L)
                        .param("name", "Updated Product")
                        .param("price", "129.99")
                        .param("description", "Updated description")
                        .param("imageUrl", "http://example.com/updated-image.jpg")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/mine"));
    }
}
