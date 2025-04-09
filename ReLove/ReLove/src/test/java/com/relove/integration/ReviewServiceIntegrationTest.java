package com.relove.integration;

import com.relove.model.dto.ReviewDTO;
import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.Product;
import com.relove.model.entity.Review;
import com.relove.model.entity.RoleEntity;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.ReviewRepo;
import com.relove.repo.RoleRepo;
import com.relove.repo.UserRepo;
import com.relove.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ReviewServiceIntegrationTest {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    private Product testProduct;
    private UserEntity testUser;

    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    public void setUp() {
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        productRepo.save(testProduct);

        String userEmail = "testuser@example.com";

        if (userRepo.findByEmail(userEmail).isEmpty()) {
            testUser = new UserEntity();
            testUser.setName("Test User");
            testUser.setEmail(userEmail);
            testUser.setPassword(new BCryptPasswordEncoder().encode("password123"));

            RoleEntity userRole = roleRepo.findByRole(UserRoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            testUser.setRoles(Set.of(userRole));
            userRepo.save(testUser);
        }else {
            testUser = userRepo.findByEmail(userEmail).get();
        }
    }

    @Test
    public void testSaveReview() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(testProduct.getId());
        reviewDTO.setComment("This is a great product!");
        reviewDTO.setRating(5);

        // Записваме ревю
        reviewService.saveReview(testUser.getEmail(), reviewDTO);

        // Проверяваме дали ревюто е записано в базата
        List<Review> reviews = reviewRepo.findByProduct(testProduct);
        assertFalse(reviews.isEmpty(), "Трябва да има поне едно ревю.");
        assertEquals(1, reviews.size(), "Трябва да има само едно ревю.");
        assertEquals("This is a great product!", reviews.get(0).getComment());
    }

    @Test
    public void testGetReviewsForProduct() {
        ReviewDTO reviewDTO1 = new ReviewDTO();
        reviewDTO1.setProductId(testProduct.getId());
        reviewDTO1.setComment("Great product!");
        reviewDTO1.setRating(5);

        ReviewDTO reviewDTO2 = new ReviewDTO();
        reviewDTO2.setProductId(testProduct.getId());
        reviewDTO2.setComment("Not bad.");
        reviewDTO2.setRating(3);

        // Записваме ревюта
        reviewService.saveReview(testUser.getEmail(), reviewDTO1);
        reviewService.saveReview(testUser.getEmail(), reviewDTO2);

        // Извличаме ревютата за продукта
        List<Review> reviews = reviewService.getReviewsForProduct(testProduct.getId());

        assertEquals(2, reviews.size(), "Трябва да има 2 ревюта.");
        assertEquals("Great product!", reviews.get(0).getComment());
        assertEquals("Not bad.", reviews.get(1).getComment());
    }

    @Test
    public void testDeleteReviewById() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(testProduct.getId());
        reviewDTO.setComment("This is a great product!");
        reviewDTO.setRating(5);

        // Записваме ревю
        reviewService.saveReview(testUser.getEmail(), reviewDTO);

        // Извличаме ревюто от базата
        List<Review> reviews = reviewRepo.findByProduct(testProduct);
        Long reviewId = reviews.get(0).getId();

        // Изтриваме ревюто
        reviewService.deleteReviewById(reviewId, testUser.getEmail());

        // Проверяваме дали ревюто е изтрито
        List<Review> remainingReviews = reviewRepo.findByProduct(testProduct);
        assertTrue(remainingReviews.isEmpty(), "Колекцията с ревюта трябва да бъде празна.");
    }



    @Test
    public void testGetProductIdByReviewId() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(testProduct.getId());
        reviewDTO.setComment("Amazing product!");
        reviewDTO.setRating(5);

        // Записваме ревю
        reviewService.saveReview(testUser.getEmail(), reviewDTO);

        // Извличаме ревюто от базата
        List<Review> reviews = reviewRepo.findByProduct(testProduct);
        Long reviewId = reviews.get(0).getId();

        // Извличаме productId по ревю
        Long productId = reviewService.getProductIdByReviewId(reviewId);
        assertEquals(testProduct.getId(), productId, "Идентификаторът на продукта не съвпада.");
    }
}

