package com.egleprojects.book_info.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RatingRequest {
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
