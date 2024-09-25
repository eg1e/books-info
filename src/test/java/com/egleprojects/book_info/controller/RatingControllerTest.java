package com.egleprojects.book_info.controller;

import com.egleprojects.book_info.dto.RatingRequest;
import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.model.BookRating;
import com.egleprojects.book_info.repository.BookRepository;
import com.egleprojects.book_info.repository.RatingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RatingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RatingRepository ratingRepository;
    private Book savedBook;

    @BeforeEach
    void setUp() {
        Book book = new Book(null, "Romeo and Juliet", "William Shakespeare", 1597,
                null, 1299, 281);

        savedBook = bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void addRating_WhenRatingValueIsValid() throws Exception {
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRating(4);

        mockMvc.perform(post("/ratings/{bookId}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.book.id", is(savedBook.getId().intValue())));
    }

    @Test
    void addRating_WhenRatingValueIsInvalid() throws Exception {
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRating(6);

        mockMvc.perform(post("/ratings/{bookId}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRating_WhenBookDoesNotExist() throws Exception {
        Long nonExistentBookId = 123L;
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRating(3);

        mockMvc.perform(post("/ratings/{bookId}", nonExistentBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRatingsForBook_WhenThereAreRatings() throws Exception {
        BookRating rating = new BookRating();
        rating.setRating(3);
        rating.setBook(savedBook);
        ratingRepository.save(rating);

        mockMvc.perform(get("/ratings/{bookId}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(3))
                .andExpect(jsonPath("$[0].book.id", is(savedBook.getId().intValue())));
    }

    @Test
    void getRatingsForBook_WhenThereAreNoRatings() throws Exception {

        mockMvc.perform(get("/ratings/{bookId}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getRatings_WhenBookDoesNotExist() throws Exception {
        Long nonExistentBookId = 123L;

        mockMvc.perform(get("/ratings/{bookId}", nonExistentBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAverageRatingForBook_WhenThereAreRatings() throws Exception {
        BookRating rating = new BookRating();
        BookRating rating1 = new BookRating();
        rating.setRating(3);
        rating1.setRating(4);
        rating.setBook(savedBook);
        rating1.setBook(savedBook);
        ratingRepository.save(rating);
        ratingRepository.save(rating1);

        mockMvc.perform(get("/ratings/average/{bookId}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3.5));
    }

    @Test
    void getAverageRatingForBook_WhenThereAreNoRatings() throws Exception {
        mockMvc.perform(get("/ratings/average/{bookId}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0.0));
    }

    @Test
    void getAverageRatingForBook_WhenBookDoesNotExist() throws Exception{
        Long nonExistentBookId = 123L;

        mockMvc.perform(get("/ratings/average/{bookId}", nonExistentBookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}