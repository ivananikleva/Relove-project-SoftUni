package com.relove.controller;

import com.relove.model.dto.ReviewDTO;
import com.relove.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public String submitReview(@ModelAttribute("reviewDTO") ReviewDTO reviewDTO, Principal principal) {
        reviewService.saveReview(principal.getName(), reviewDTO);
        return "redirect:/products/details/" + reviewDTO.getProductId();
    }


    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Principal principal) {
        Long productId = reviewService.getProductIdByReviewId(id);
        reviewService.deleteReviewById(id, principal.getName());
        return "redirect:/products/details/" + productId;
    }
}
