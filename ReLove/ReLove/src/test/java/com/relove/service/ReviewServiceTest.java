package com.relove.service;

import com.relove.model.dto.ReviewDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.Review;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.ReviewRepo;
import com.relove.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepo reviewRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ReviewService reviewService;

    private UserEntity user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("user@relove.bg");

        product = new Product();
        product.setId(1L);
    }

    @Test
    void testSaveReviewStoresCorrectData() {
        ReviewDTO dto = new ReviewDTO();
        dto.setProductId(1L);
        dto.setComment("Great!");
        dto.setRating(5);

        when(userRepo.findByEmail("user@relove.bg")).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        reviewService.saveReview("user@relove.bg", dto);

        verify(reviewRepo).save(argThat(review ->
                review.getUser().equals(user) &&
                        review.getProduct().equals(product) &&
                        review.getComment().equals("Great!") &&
                        review.getRating() == 5
        ));
    }

    @Test
    void testGetReviewsForProductReturnsCorrectList() {
        Review review1 = new Review();
        review1.setComment("Nice");

        Review review2 = new Review();
        review2.setComment("Excellent");

        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepo.findByProduct(product)).thenReturn(List.of(review1, review2));

        List<Review> result = reviewService.getReviewsForProduct(1L);

        assertEquals(2, result.size());
        assertEquals("Nice", result.get(0).getComment());
        assertEquals("Excellent", result.get(1).getComment());
    }

    @Test
    void testDeleteReviewByIdDeletesIfAuthorized() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);

        when(reviewRepo.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReviewById(1L, "user@relove.bg");

        verify(reviewRepo).delete(review);
    }

    @Test
    void testDeleteReviewByIdThrowsIfUnauthorized() {
        Review review = new Review();
        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@relove.bg");
        review.setUser(otherUser);

        when(reviewRepo.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(RuntimeException.class, () ->
                reviewService.deleteReviewById(1L, "user@relove.bg"));
    }

    @Test
    void testGetProductIdByReviewIdReturnsCorrectId() {
        Review review = new Review();
        Product product = new Product();
        product.setId(5L);
        review.setProduct(product);

        when(reviewRepo.findById(1L)).thenReturn(Optional.of(review));

        Long result = reviewService.getProductIdByReviewId(1L);

        assertEquals(5L, result);
    }
}
