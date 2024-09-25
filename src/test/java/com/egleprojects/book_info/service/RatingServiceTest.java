package com.egleprojects.book_info.service;

import com.egleprojects.book_info.filter.ResourceNotFoundException;
import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.model.BookRating;
import com.egleprojects.book_info.repository.BookRepository;
import com.egleprojects.book_info.repository.RatingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private RatingService ratingService;
    private Book book;
    private BookRating rating;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        book = new Book(1L, "Romeo and Juliet", "William Shakespeare", 1597,
                null, 1299, 281);
        rating = new BookRating(1L, 4, book);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addRating_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(ratingRepository.save(any(BookRating.class))).thenReturn(rating);

        BookRating result = ratingService.addRating(1L, 4);

        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("Romeo and Juliet", result.getBook().getTitle());
        verify(bookRepository, times(1)).findById(1L);
        verify(ratingRepository, times(1)).save(any(BookRating.class));
    }

    @Test
    void addRating_WhenBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.addRating(1L,4);
        });

        assertEquals("Book with an ID 1 is not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(ratingRepository, times(0)).save(any(BookRating.class));
    }

    @Test
    void getRatings_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(ratingRepository.findBookRatingsByBookId(1L)).thenReturn(Arrays.asList(rating));

        List<BookRating> result = ratingService.getRatings(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4,result.get(0).getRating());
        verify(bookRepository, times(1)).findById(1L);
        verify(ratingRepository, times(1)).findBookRatingsByBookId(1L);
    }

    @Test
    void getRatings_WhenBookNotFound(){
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.getRatings(1L);
        });

        assertEquals("Book with ID 1 not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(ratingRepository, times(0)).findBookRatingsByBookId(1L);
    }

    @Test
    void getAverageRating_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(ratingRepository.findAverageRatingByBookId(1L)).thenReturn(4.5);

        double result = ratingService.getAverageRating(1L);

        assertEquals(4.5, result);
        verify(bookRepository, times(1)).findById(1L);
        verify(ratingRepository, times(1)).findAverageRatingByBookId(1L);
    }

    @Test
    void getAverageRating_WhenBookNotFound(){
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.getAverageRating(1L);
        });

        assertEquals("Book with ID 1 not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(ratingRepository,times(0)).findBookRatingsByBookId(1L);
    }

    @Test
    void getAverageRating_WhenBookFoundAndHasNoRatings(){
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(ratingRepository.findAverageRatingByBookId(1L)).thenReturn(0.0);

        double result = ratingService.getAverageRating(1L);

        assertEquals(0.0, result);
        verify(ratingRepository, times(1)).findAverageRatingByBookId(1L);
    }
}