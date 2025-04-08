package com.relove.service;

import com.relove.model.dto.ProductAddDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepo productRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetAllProductsReturnsCorrectList() {
        Product p1 = new Product();
        p1.setName("Рокля");

        Product p2 = new Product();
        p2.setName("Яке");

        when(productRepo.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Рокля", result.get(0).getName());
        assertEquals("Яке", result.get(1).getName());

        verify(productRepo, times(1)).findAll();
    }

    @Test
    void testGetProductByIdReturnsCorrectProduct() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Vintage Jacket");

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(productId);

        assertTrue(result.isPresent());
        assertEquals("Vintage Jacket", result.get().getName());
        verify(productRepo).findById(productId);
    }

    @Test
    void testAddProductSuccessfully() throws IOException {
        String userEmail = "user@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(userEmail);

        ProductAddDTO dto = new ProductAddDTO();
        dto.setName("Handmade Bag");
        dto.setDescription("A lovely bag");
        dto.setPrice(59.99);

        MultipartFile mockFile = mock(MultipartFile.class);
        dto.setImage(mockFile);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("image.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test image".getBytes()));

        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));

        productService.addProduct(dto, userEmail);

        verify(productRepo).save(any(Product.class));
    }

    @Test
    void testDeleteProductByIdDeletesProductAndImage() throws IOException {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setImageUrl("/uploads/test-image.jpg");

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        Path filePath = Paths.get("uploads/test-image.jpg");
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, "dummy data".getBytes());

        productService.deleteProductById(productId);

        assertFalse(Files.exists(filePath), "Файлът трябва да е изтрит");
        verify(productRepo).delete(product);
    }

    @Test
    void testGetFeaturedProductsReturnsLatestProducts() {
        Product p1 = new Product();
        p1.setName("Дънки");

        Product p2 = new Product();
        p2.setName("Палто");

        when(productRepo.findTop6ByOrderByIdDesc()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.getFeaturedProducts();

        assertEquals(2, result.size());
        assertEquals("Дънки", result.get(0).getName());
        assertEquals("Палто", result.get(1).getName());

        verify(productRepo).findTop6ByOrderByIdDesc();
    }

    @Test
    void testGetProductForEditReturnsCorrectDTO() {
        Long productId = 1L;
        String email = "user@example.com";

        UserEntity seller = new UserEntity();
        seller.setEmail(email);

        Product product = new Product();
        product.setId(productId);
        product.setName("Шал");
        product.setDescription("Зимен шал");
        product.setPrice(29.99);
        product.setSeller(seller);

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        ProductAddDTO dto = productService.getProductForEdit(productId, email);

        assertEquals("Шал", dto.getName());
        assertEquals("Зимен шал", dto.getDescription());
        assertEquals(29.99, dto.getPrice());
    }

    @Test
    void testUpdateProductWithoutImageUpdatesFieldsCorrectly() {
        Long productId = 1L;
        String userEmail = "user@example.com";

        UserEntity seller = new UserEntity();
        seller.setEmail(userEmail);

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Старо име");
        existingProduct.setDescription("Стара информация");
        existingProduct.setPrice(10.0);
        existingProduct.setImageUrl("/uploads/old-image.jpg");
        existingProduct.setSeller(seller);

        ProductAddDTO updatedDto = new ProductAddDTO();
        updatedDto.setName("Ново име");
        updatedDto.setDescription("Нова информация");
        updatedDto.setPrice(25.50);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true); // Без нова снимка
        updatedDto.setImage(mockFile);

        when(productRepo.findById(productId)).thenReturn(Optional.of(existingProduct));

        productService.updateProduct(productId, updatedDto, userEmail);

        assertEquals("Ново име", existingProduct.getName());
        assertEquals("Нова информация", existingProduct.getDescription());
        assertEquals(25.50, existingProduct.getPrice());
        assertEquals("/uploads/old-image.jpg", existingProduct.getImageUrl()); // снимката не се променя

        verify(productRepo).save(existingProduct);
    }




}
