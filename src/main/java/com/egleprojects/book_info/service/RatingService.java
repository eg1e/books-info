package com.egleprojects.book_info.service;

import com.egleprojects.book_info.filter.ResourceNotFoundException;
import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.model.BookRating;
import com.egleprojects.book_info.repository.BookRepository;
import com.egleprojects.book_info.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookRepository bookRepository;

    public BookRating addRating(Long bookId, int value) {
        BookRating result = null;

        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            result = new BookRating();
            result.setRating(value);
            result.setBook(optionalBook.get());
            ratingRepository.save(result);
        } else {
            throw new ResourceNotFoundException("Book with an ID " + bookId + " is not found");
        }

        return result;
    }

    public List<BookRating> getRatings(Long bookId) {
        bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " not found"));
        return ratingRepository.findBookRatingsByBookId(bookId);
    }

    public double getAverageRating(Long bookId) {
        bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " not found"));
        return ratingRepository.findAverageRatingByBookId(bookId);
    }
}
