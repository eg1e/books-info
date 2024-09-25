package com.egleprojects.book_info.service;

import com.egleprojects.book_info.filter.ResourceNotFoundException;
import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;


import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;
    AutoCloseable autoCloseable;
    Book book;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        book = new Book(1L, "Romeo and Juliet", "William Shakespeare", 1597,
                null, 1299, 281);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addBook() {
        when(bookRepository.save(book)).thenReturn(book);
        Book result = bookService.addBook(book);

        assertNotNull(result);
        assertEquals("Romeo and Juliet", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_WhenBookFound() {
        Book updatedBook = new Book(1L, "Romeo and Juliet", "William Shakespeare",
                1597, null, 1599, 281);

        when(bookRepository.findById(updatedBook.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(updatedBook);

        Book result = bookService.updateBook(updatedBook);

        assertNotNull(result);
        assertEquals("Romeo and Juliet", result.getTitle());
        verify(bookRepository, times(1)).findById(updatedBook.getId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateBook_WhenBookNotFound(){
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(book);
        });

        assertEquals("Book with an ID 1 is not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void getBook_GetAllBooks(){
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        bookList.add(new Book(2L, "Harry Potter and the Sorcerer’s Stone", "J.K. Rowling",
                1997, null, 2399, 333));
        bookList.add(new Book(3L, "Hamlet", "William Shakespeare",
                1601, null, 1399, 289));
        Map<String, Object> allBooks = new HashMap<>();

        when(bookRepository.findAll(any(Specification.class))).thenReturn(bookList);

        List<Book> result = bookService.getBook(allBooks);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Romeo and Juliet", result.get(0).getTitle());
        assertEquals("Harry Potter and the Sorcerer’s Stone", result.get(1).getTitle());
        assertEquals("Hamlet", result.get(2).getTitle());
        verify(bookRepository, times(1)).findAll(any(Specification.class));
    }


}