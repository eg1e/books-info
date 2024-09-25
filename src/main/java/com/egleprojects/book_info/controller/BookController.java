package com.egleprojects.book_info.controller;

import com.egleprojects.book_info.filter.ResourceNotFoundException;
import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam Map<String, Object> filters) {
        return new ResponseEntity<>(bookService.getBook(filters), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> addBooks(@RequestBody Book book) {
        return new ResponseEntity<>(bookService.addBook(book), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Book> updateBooks(@RequestBody Book book) {
        Book result = null;
        try {
            result = bookService.updateBook(book);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
