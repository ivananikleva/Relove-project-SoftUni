package com.relove.integration;

import com.relove.model.dto.UserRegisterDTO;
import com.relove.model.dto.UserRoleEnum;
import com.relove.model.dto.UserViewDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import com.relove.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ProductRepo productRepo;

    private UserEntity testUser;

    @BeforeEach
    public void setUp() {
        if (roleRepo.count() == 0) {
            RoleEntity adminRole = new RoleEntity();
            adminRole.setRole(UserRoleEnum.ADMIN);
            roleRepo.save(adminRole);

            RoleEntity userRole = new RoleEntity();
            userRole.setRole(UserRoleEnum.USER);
            roleRepo.save(userRole);
        }

        if (userRepo.findByEmail("testuser@example.com").isEmpty()) {
            testUser = new UserEntity();
            testUser.setName("Test User");
            testUser.setEmail("testuser@example.com");
            testUser.setPassword(new BCryptPasswordEncoder().encode("password123"));

            RoleEntity userRole = roleRepo.findByRole(UserRoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            testUser.setRoles(Set.of(userRole));
            userRepo.save(testUser);
        } else {
            testUser = userRepo.findByEmail("testuser@example.com").orElseThrow();
        }
    }

    @Test
    public void testRegister() {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setName("New User");
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPassword("newpassword123");

        boolean isRegistered = userService.register(registerDTO);

        assertTrue(isRegistered);

        UserEntity user = userRepo.findByEmail("newuser@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertTrue(user.getRoles().stream()
                .anyMatch(role -> role.getRole() == UserRoleEnum.USER));

        assertEquals("New User", user.getName());
        assertNotNull(user.getPassword());
    }


    @Test
    public void testGetUserViewByEmail() {
        UserViewDTO userViewDTO = userService.getUserViewByEmail("testuser@example.com");

        assertNotNull(userViewDTO);
        assertEquals("Test User", userViewDTO.getName());
        assertEquals("testuser@example.com", userViewDTO.getEmail());
    }

    @Test
    public void testUpdateProfile() {
        UserRegisterDTO updateDTO = new UserRegisterDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPassword("newpassword123");

        userService.updateProfile("testuser@example.com", updateDTO);

        UserEntity updatedUser = userRepo.findByEmail("updated@example.com").orElseThrow();

        assertEquals("Updated Name", updatedUser.getName());
        assertNotEquals("password123", updatedUser.getPassword());
    }

    @Test
    public void testAddFavoriteProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setSeller(testUser);
        productRepo.save(product);

        userService.addFavoriteProduct(testUser.getEmail(), product.getId());

        Set<Product> favoriteProducts = userService.getFavoriteProducts(testUser.getEmail());

        assertTrue(favoriteProducts.contains(product));
    }

    @Test
    public void testRemoveFavoriteProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setSeller(testUser);
        productRepo.save(product);

        userService.addFavoriteProduct(testUser.getEmail(), product.getId());
        userService.removeFavoriteProduct(testUser.getEmail(), product.getId());

        Set<Product> favoriteProducts = userService.getFavoriteProducts(testUser.getEmail());

        assertFalse(favoriteProducts.contains(product));
    }

    @Test
    public void testIsProductFavorite() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setSeller(testUser);
        productRepo.save(product);

        userService.addFavoriteProduct(testUser.getEmail(), product.getId());

        assertTrue(userService.isProductFavorite(testUser.getEmail(), product.getId()));
    }



    @Test
    public void testToggleUserRole() {
        assertEquals(UserRoleEnum.USER, testUser.getRoles().iterator().next().getRole());

        userService.toggleUserRole(testUser.getId());

        testUser = userRepo.findById(testUser.getId()).orElseThrow();

        assertEquals(UserRoleEnum.ADMIN, testUser.getRoles().iterator().next().getRole());

        userService.toggleUserRole(testUser.getId());

        testUser = userRepo.findById(testUser.getId()).orElseThrow();

        assertEquals(UserRoleEnum.USER, testUser.getRoles().iterator().next().getRole());
    }


}

