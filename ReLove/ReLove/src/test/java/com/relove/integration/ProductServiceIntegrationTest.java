package com.relove.integration;


import com.relove.model.dto.ProductAddDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.UserRepo;
import com.relove.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    private UserEntity user;

    @BeforeEach
    public void setUp() {
        user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("testuser");
        userRepo.save(user);
    }

    @Test
    public void testAddProduct() throws IOException {
        MockMultipartFile imageFile = new MockMultipartFile("image", "testImage.jpg", "image/jpeg", "test image content".getBytes());

        ProductAddDTO dto = new ProductAddDTO();
        dto.setName("Test Product");
        dto.setDescription("Description for test product");
        dto.setPrice(100.0);
        dto.setImage(imageFile);

        productService.addProduct(dto, user.getEmail());

        Optional<Product> productOptional = productRepo.findByName("Test Product");

        assertTrue(productOptional.isPresent(), "Продуктът трябва да е намерен.");

        Product product = productOptional.get();
        assertEquals("Test Product", product.getName());
        assertEquals("Description for test product", product.getDescription());
    }


    @Test
    @WithMockUser(username = "test@example.com")
    public void testGetProductsByCurrentUser() {
        Product product = new Product();
        product.setName("User Product");
        product.setDescription("Product for test user");
        product.setPrice(150.0);
        product.setSeller(user);
        productRepo.save(product);

        List<Product> products = productService.getProductsByCurrentUser();

        assertFalse(products.isEmpty());
        assertEquals("User Product", products.get(0).getName());
    }


    @Test
    public void testDeleteProductById() {
        Product product = new Product();
        product.setName("Product to delete");
        product.setDescription("Product for deletion test");
        product.setPrice(200.0);
        product.setSeller(user);
        productRepo.save(product);

        Long productId = product.getId();

        productService.deleteProductById(productId);

        assertFalse(productRepo.findById(productId).isPresent());
    }

    @Test
    public void testGetFeaturedProducts() {
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setName("Featured Product " + i);
            product.setDescription("Featured product description " + i);
            product.setPrice(500.0);
            product.setSeller(user);
            productRepo.save(product);
        }

        List<Product> featuredProducts = productService.getFeaturedProducts();

        assertEquals(6, featuredProducts.size());
    }

    @Test
    public void testGetProductForEdit() {
        Product product = new Product();
        product.setName("Editable Product");
        product.setDescription("Description for editable product");
        product.setPrice(600.0);
        product.setSeller(user);
        productRepo.save(product);

        ProductAddDTO productForEdit = productService.getProductForEdit(product.getId(), user.getEmail());

        assertEquals("Editable Product", productForEdit.getName());
        assertEquals("Description for editable product", productForEdit.getDescription());
    }

    @Test
    public void testUpdateProduct() throws IOException {
        Product product = new Product();
        product.setName("Old Product");
        product.setDescription("Old description");
        product.setPrice(700.0);
        product.setSeller(user);
        productRepo.save(product);

        MockMultipartFile imageFile = new MockMultipartFile("image", "updatedImage.jpg", "image/jpeg", "updated image content".getBytes());
        ProductAddDTO dto = new ProductAddDTO();
        dto.setName("Updated Product");
        dto.setDescription("Updated description");
        dto.setPrice(750.0);
        dto.setImage(imageFile);

        productService.updateProduct(product.getId(), dto, user.getEmail());

        Product updatedProduct = productRepo.findById(product.getId()).orElseThrow();
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals("Updated description", updatedProduct.getDescription());
    }


}
