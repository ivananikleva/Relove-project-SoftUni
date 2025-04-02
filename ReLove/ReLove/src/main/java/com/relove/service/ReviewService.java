package com.relove.service;

import com.relove.model.dto.ReviewDTO;
import com.relove.model.entity.Product;
import com.relove.model.entity.Review;
import com.relove.model.entity.UserEntity;
import com.relove.repo.ProductRepo;
import com.relove.repo.ReviewRepo;
import com.relove.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    public ReviewService(ReviewRepo reviewRepo, ProductRepo productRepo, UserRepo userRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public void saveReview(String userEmail, ReviewDTO dto) {
        UserEntity user = userRepo.findByEmail(userEmail).orElseThrow();
        Product product = productRepo.findById(dto.getProductId()).orElseThrow();

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());

        reviewRepo.save(review);
    }

    public List<Review> getReviewsForProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow();
        return reviewRepo.findByProduct(product);
    }
}
