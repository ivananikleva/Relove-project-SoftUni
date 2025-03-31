package com.relove.service;

import com.relove.model.dto.ProductAddDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    public ProductService(ProductRepo productRepo, UserRepo userRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public void addProduct(ProductAddDTO dto, String userEmail) {
        UserEntity seller = userRepo.findByEmail(userEmail)
                .orElseThrow();

        if (dto.getImage().isEmpty()) {
            throw new RuntimeException("Файлът е празен!");
        }

        String originalFilename = dto.getImage().getOriginalFilename();
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String fileName = UUID.randomUUID() + "_" + sanitizedFilename;

        Path uploadPath = Paths.get("uploads");

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(dto.getImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Неуспешно качване на изображение.", e);
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl("/uploads/" + fileName);
        product.setSeller(seller);

        productRepo.save(product);
    }

    public List<Product> getProductsByCurrentUser() {
        String email = getCurrentUserEmail();

        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return productRepo.findAllBySeller(user);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        throw new RuntimeException("User not authenticated");
    }


    public void deleteProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String imageUrl = product.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
            String fileName = imageUrl.substring("/uploads/".length());
            Path filePath = Paths.get("uploads", fileName);

            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.out.println("⚠️ Неуспешно изтриване на изображението: " + e.getMessage());
            }
        }

        productRepo.delete(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    public List<Product> getFeaturedProducts() {
        return productRepo.findTop6ByOrderByIdDesc();
    }

    public ProductAddDTO getProductForEdit(Long productId, String email) {
        Product product = productRepo.findById(productId)
                .orElseThrow();

        if (!product.getSeller().getEmail().equals(email)) {
            throw new RuntimeException("User not authenticated");
        }

        ProductAddDTO dto = new ProductAddDTO();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());

        return dto;
    }

    public void updateProduct(Long productId, ProductAddDTO dto, String email) {
        Product product = productRepo.findById(productId)
                .orElseThrow();

        if (!product.getSeller().getEmail().equals(email)) {
            throw new RuntimeException("User not authenticated");
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        if (!dto.getImage().isEmpty()) {
            // Запиши новата снимка
            String fileName = UUID.randomUUID() + "_" + dto.getImage().getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(dto.getImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Грешка при запис на изображение", e);
            }
        }

        productRepo.save(product);
    }





}
