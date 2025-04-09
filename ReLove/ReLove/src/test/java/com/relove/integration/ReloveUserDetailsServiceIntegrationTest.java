package com.relove.integration;


import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import com.relove.service.ReLoveUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ReloveUserDetailsServiceIntegrationTest {

    @Autowired
    private ReLoveUserDetailsService userDetailsService;

    @Autowired
    private UserRepo userRepo;

    private UserEntity testUser;

    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    public void setUp() {
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
    public void testLoadUserByUsername_Success() {
        // Тест за успешното зареждане на потребител
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser@example.com");

        assertNotNull(userDetails, "UserDetails не трябва да е null.");
        assertEquals("testuser@example.com", userDetails.getUsername(), "Имейлът на потребителя не съвпада.");
        assertTrue(userDetails.getAuthorities().size() > 0, "Потребителят трябва да има поне една роля.");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Тест за случая, когато потребителят не съществува
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentuser@example.com");
        }, "Трябва да се хвърли UsernameNotFoundException.");
    }
}
