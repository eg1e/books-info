package com.egleprojects.book_info.repository;

import com.egleprojects.book_info.model.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book( null, "Lord of the Flies", "William Golding", 1954, null, 999, 182);
        book = bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
        book = null;
        bookRepository.deleteAll();
    }

    @Test
    public void findBookById(){
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertTrue(foundBook.isPresent());
        assertEquals("Lord of the Flies", foundBook.get().getTitle());
        assertEquals(1954, foundBook.get().getPublishedYear());
        assertEquals(999, foundBook.get().getPrice());
        assertEquals(182, foundBook.get().getPages());
    }
}
