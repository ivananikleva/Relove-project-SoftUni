package com.relove.service;

import com.relove.model.dto.UserRegisterDTO;
import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.Product;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterFirstUserBecomesAdmin() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setName("admin");
        dto.setEmail("admin@relove.bg");
        dto.setPassword("secret");

        RoleEntity adminRole = new RoleEntity();
        adminRole.setRole(UserRoleEnum.ADMIN);

        when(userRepo.findByNameOrEmail(dto.getName(), dto.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepo.count()).thenReturn(0L);
        when(roleRepo.findByRole(UserRoleEnum.ADMIN))
                .thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encoded-password");


        boolean result = userService.register(dto);

        assertTrue(result);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();

        assertEquals("admin", savedUser.getName());
        assertEquals("admin@relove.bg", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(adminRole));
    }

    @Test
    void testToggleUserRoleFromUserToAdmin() {
        Long userId = 1L;
        RoleEntity userRole = new RoleEntity();
        userRole.setRole(UserRoleEnum.USER);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setRole(UserRoleEnum.ADMIN);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setRoles(new HashSet<>(Set.of(userRole)));

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepo.findByRole(UserRoleEnum.ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepo.findByRole(UserRoleEnum.USER)).thenReturn(Optional.of(userRole));

        userService.toggleUserRole(userId);

        assertTrue(user.getRoles().contains(adminRole));
        assertTrue(user.getRoles().contains(userRole));
        verify(userRepo).save(user);
    }

    @Test
    void testToggleUserRoleFromAdminToUser() {
        Long userId = 2L;
        RoleEntity adminRole = new RoleEntity();
        adminRole.setRole(UserRoleEnum.ADMIN);

        RoleEntity userRole = new RoleEntity();
        userRole.setRole(UserRoleEnum.USER);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setRoles(new HashSet<>(Set.of(adminRole)));

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepo.findByRole(UserRoleEnum.ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepo.findByRole(UserRoleEnum.USER)).thenReturn(Optional.of(userRole));

        userService.toggleUserRole(userId);

        assertTrue(user.getRoles().contains(userRole));
        assertFalse(user.getRoles().contains(adminRole));
        verify(userRepo).save(user);
    }

    @Test
    void testRegisterUserWhenOthersExistGetsUserRole() {

        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setName("testUser");
        dto.setEmail("user@relove.bg");
        dto.setPassword("secret");

        RoleEntity userRole = new RoleEntity();
        userRole.setRole(UserRoleEnum.USER);

        when(userRepo.findByNameOrEmail(dto.getName(), dto.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepo.count()).thenReturn(5L); // вече има потребители
        when(roleRepo.findByRole(UserRoleEnum.USER))
                .thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encoded-password");


        boolean result = userService.register(dto);


        assertTrue(result);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();

        assertEquals("testUser", savedUser.getName());
        assertEquals("user@relove.bg", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(userRole));
    }

    @Test
    void testRegisterFailsWhenUserAlreadyExists() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setName("duplicate");
        dto.setEmail("duplicate@relove.bg");

        UserEntity existingUser = new UserEntity();
        existingUser.setName(dto.getName());
        existingUser.setEmail(dto.getEmail());

        when(userRepo.findByNameOrEmail(dto.getName(), dto.getEmail()))
                .thenReturn(Optional.of(existingUser));

        boolean result = userService.register(dto);

        assertFalse(result);
        verify(userRepo, never()).save(any());
    }

    @Test
    void testUpdateProfileChangesNameEmailAndPassword() {
        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("old@relove.bg");
        existingUser.setName("Old Name");
        existingUser.setPassword("old-password");

        UserRegisterDTO updatedData = new UserRegisterDTO();
        updatedData.setEmail("new@relove.bg");
        updatedData.setName("New Name");
        updatedData.setPassword("new-password");

        when(userRepo.findByEmail("old@relove.bg")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        userService.updateProfile("old@relove.bg", updatedData);

        assertEquals("New Name", existingUser.getName());
        assertEquals("new@relove.bg", existingUser.getEmail());
        assertEquals("encoded-new-password", existingUser.getPassword());
        verify(userRepo).save(existingUser);
    }

    @Test
    void testGetUserViewByEmailReturnsCorrectData() {
        UserEntity user = new UserEntity();
        user.setName("View Test");
        user.setEmail("view@relove.bg");

        when(userRepo.findByEmail("view@relove.bg"))
                .thenReturn(Optional.of(user));

        var result = userService.getUserViewByEmail("view@relove.bg");

        assertEquals("View Test", result.getName());
        assertEquals("view@relove.bg", result.getEmail());
    }

    @Test
    void testFindByEmailReturnsUser() {
        UserEntity user = new UserEntity();
        user.setEmail("find@relove.bg");

        when(userRepo.findByEmail("find@relove.bg")).thenReturn(Optional.of(user));

        UserEntity result = userService.findByEmail("find@relove.bg");

        assertEquals("find@relove.bg", result.getEmail());
    }

    @Test
    void testAddFavoriteProduct() {
        String email = "user@relove.bg";
        Long productId = 1L;

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFavoriteProducts(new HashSet<>());

        Product product = new Product();
        product.setId(productId);

        when(userRepo.findByEmailWithFavorites(email)).thenReturn(Optional.of(user));
        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        userService.addFavoriteProduct(email, productId);

        assertTrue(user.getFavoriteProducts().contains(product));
        verify(userRepo).save(user);
    }

    @Test
    void testRemoveFavoriteProduct() {
        String email = "user@relove.bg";
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);

        Set<Product> favorites = new HashSet<>();
        favorites.add(product);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFavoriteProducts(favorites);

        when(userRepo.findByEmailWithFavorites(email)).thenReturn(Optional.of(user));

        userService.removeFavoriteProduct(email, productId);

        assertFalse(user.getFavoriteProducts().contains(product));
        verify(userRepo).save(user);
    }

    @Test
    void testGetFavoriteProducts() {
        String email = "user@relove.bg";

        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        Set<Product> favorites = Set.of(product1, product2);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFavoriteProducts(favorites);

        when(userRepo.findByEmailWithFavorites(email)).thenReturn(Optional.of(user));

        Set<Product> result = userService.getFavoriteProducts(email);

        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
    }

    @Test
    void testIsProductFavoriteReturnsTrue() {
        String email = "user@relove.bg";
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFavoriteProducts(Set.of(product));

        when(userRepo.findByEmailWithFavorites(email)).thenReturn(Optional.of(user));

        assertTrue(userService.isProductFavorite(email, productId));
    }




}

