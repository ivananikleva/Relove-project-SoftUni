package com.relove.service;

import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.model.dto.UserRoleEnum;
import com.relove.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ReLoveUserDetailsService userDetailsService;

    @Test
    void testLoadUserByUsernameReturnsCorrectUserDetails() {
        // Arrange
        String email = "admin@relove.bg";
        String password = "encoded-password";

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setRole(UserRoleEnum.ADMIN);
        user.setRoles(Set.of(adminRole));

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsernameThrowsExceptionWhenUserNotFound() {
        // Arrange
        String email = "missing@relove.bg";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));
    }
}
