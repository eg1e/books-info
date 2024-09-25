package com.egleprojects.book_info.controller;

import com.egleprojects.book_info.dto.RatingRequest;
import com.egleprojects.book_info.filter.ResourceNotFoundException;
import com.egleprojects.book_info.model.BookRating;
import com.egleprojects.book_info.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/{bookId}")
    public ResponseEntity<BookRating> addRating(@PathVariable Long bookId, @Valid @RequestBody RatingRequest ratingRequest) {
        BookRating result = null;
        try {
            result = ratingService.addRating(bookId, ratingRequest.getRating());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<List<BookRating>> getRatingsForBook(@PathVariable Long bookId) {
        try {
            List<BookRating> ratings = ratingService.getRatings(bookId);
            if (ratings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/average/{bookId}")
    public ResponseEntity<Double> getAverageRatingForBook(@PathVariable Long bookId) {
        try {
            Double averageRating = ratingService.getAverageRating(bookId);
            return new ResponseEntity<>(averageRating, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
