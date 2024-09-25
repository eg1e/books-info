package com.egleprojects.book_info.repository;

import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.model.BookRating;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
public class RatingRepositoryTest {
    @Autowired
    private RatingRepository ratingRepository;
    BookRating bookRating;
    Book book;
    @Autowired
    BookRepository bookRepository;
    Long nonExistingBookId = 15L;

    @BeforeEach
    void setUp() {
        book = new Book(null, "Lord of the Flies", "William Golding", 1954, null, 999, 182);
        book = bookRepository.save(book);

        bookRating = new BookRating(null, 4, book);
        ratingRepository.save(bookRating);
    }

    @AfterEach
    void tearDown() {
        bookRating = null;
        ratingRepository.deleteAll();
        bookRepository.deleteAll();
    }


    @Test
    void findBookRatingsByBookId_WhenFound(){
        List<BookRating> ratingList = ratingRepository.findBookRatingsByBookId(book.getId());

        assertFalse(ratingList.isEmpty());
        assertEquals(4, ratingList.get(0).getRating());
    }

    @Test
    void findBookRatingsByBookId_WhenNotFound(){
        List<BookRating> ratingList = ratingRepository.findBookRatingsByBookId(nonExistingBookId);
        assertTrue(ratingList.isEmpty());
    }

    @Test
    void findBookRatingsByBookId_WhenThereAreNoRatingsForBook(){
        book = new Book(null, "Romeo and Juliet", "William Shakespeare", 1597, null, 1299, 281);
        book = bookRepository.save(book);
        List<BookRating> ratingList = ratingRepository.findBookRatingsByBookId(book.getId());
        assertTrue(ratingList.isEmpty());
    }

    @Test
    void findAverageRatingByBookId_WhenOneRatingFound(){
        Double averageRating = ratingRepository.findAverageRatingByBookId(book.getId());
        assertEquals(averageRating, 4);
    }

    @Test
    void findAverageRatingByBookId_WhenFewRatingsFound(){
        bookRating = new BookRating(null, 5, book);
        ratingRepository.save(bookRating);
        Double averageRating = ratingRepository.findAverageRatingByBookId(book.getId());

        assertEquals(averageRating, 4.5);
    }

    @Test
    void findAverageRatingByBookId_WhenZeroRatingsFound(){
        book = new Book(null, "Romeo and Juliet", "William Shakespeare", 1597, null, 1299, 281);
        book = bookRepository.save(book);

        Double averageRating = ratingRepository.findAverageRatingByBookId(book.getId());

        assertEquals(0, averageRating);
    }

    @Test
    void findAverageRatingByBookId_WhenBookNotFound(){
        Double averageRating = ratingRepository.findAverageRatingByBookId(nonExistingBookId);
        assertEquals(0, averageRating);
    }
}
