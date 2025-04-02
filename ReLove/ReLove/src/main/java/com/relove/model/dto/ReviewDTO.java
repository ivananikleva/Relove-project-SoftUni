package com.relove.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewDTO {
    private int rating;

    @NotBlank
    private String comment;

    private Long productId;

    public ReviewDTO() {}

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
