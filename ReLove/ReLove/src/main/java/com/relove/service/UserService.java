package com.relove.service;

import com.relove.model.dto.UserRegisterDTO;
import com.relove.model.dto.UserRoleEnum;
import com.relove.model.dto.UserViewDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepo productRepo;
    private final RoleRepo roleRepo;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, ProductRepo productRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.productRepo = productRepo;
        this.roleRepo = roleRepo;
    }

    public boolean register(UserRegisterDTO data) {
        Optional<UserEntity> existingUser = userRepo
                .findByNameOrEmail(data.getName(), data.getEmail());

        if (existingUser.isPresent()) {
            return false;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setName(data.getName());
        userEntity.setEmail(data.getEmail());
        userEntity.setPassword(passwordEncoder.encode(data.getPassword()));


        Set<RoleEntity> roles = new HashSet<>();
        if (userRepo.count() == 0) {
            RoleEntity adminRole = roleRepo.findByRole(UserRoleEnum.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            roles.add(adminRole);
        } else {
            RoleEntity userRole = roleRepo.findByRole(UserRoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            roles.add(userRole);
        }

        userEntity.setRoles(roles);

        userRepo.save(userEntity);

        return true;
    }

    public UserViewDTO getUserViewByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(user -> new UserViewDTO(user.getName(), user.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    public UserEntity findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public void updateProfile(String email, UserRegisterDTO data) {
        UserEntity user = userRepo.findByEmail(email).orElseThrow();

        user.setName(data.getName());
        user.setEmail(data.getEmail());

        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(data.getPassword()));
        }

        userRepo.save(user);
    }

    public void addFavoriteProduct(String email, Long productId) {
        UserEntity user = userRepo.findByEmailWithFavorites(email).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();

        user.getFavoriteProducts().add(product);
        userRepo.save(user);
    }


    public Set<Product> getFavoriteProducts(String email) {
        UserEntity user = userRepo.findByEmailWithFavorites(email).orElseThrow();
        return user.getFavoriteProducts();
    }


    public void removeFavoriteProduct(String email, Long productId) {
        UserEntity user = userRepo.findByEmailWithFavorites(email).orElseThrow();
        user.getFavoriteProducts().removeIf(product -> product.getId().equals(productId));
        userRepo.save(user);
    }


    @Transactional
    public boolean isProductFavorite(String email, Long productId) {
        UserEntity user = userRepo.findByEmailWithFavorites(email).orElseThrow();
        return user.getFavoriteProducts()
                .stream()
                .anyMatch(product -> product.getId().equals(productId));
    }

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public void toggleUserRole(Long userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<RoleEntity> roles = user.getRoles();

        RoleEntity adminRole = roleRepo.findByRole(UserRoleEnum.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        RoleEntity userRole = roleRepo.findByRole(UserRoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (roles.contains(adminRole)) {
            roles.clear();
            roles.add(userRole);
        } else {
            roles.add(adminRole);
        }

        user.setRoles(roles);
        userRepo.save(user);
    }

}
