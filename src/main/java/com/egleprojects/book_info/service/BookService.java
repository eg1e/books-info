package com.egleprojects.book_info.service;

import com.egleprojects.book_info.filter.GenericSpecificationBuilder;
import com.egleprojects.book_info.filter.ResourceNotFoundException;
import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.repository.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final GenericSpecificationBuilder<Book> specificationBuilder;


    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.specificationBuilder = new GenericSpecificationBuilder<>();
    }

    public List<Book> getBook(Map<String, Object> filters) {
        var specification = getSpecification(filters);
        return bookRepository.findAll(specification);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Book updatedBookData) {
        Optional<Book> optionalBook = bookRepository.findById(updatedBookData.getId());

        if (optionalBook.isPresent()) {
            Book existingBook = optionalBook.get();

            existingBook.setTitle(updatedBookData.getTitle());
            existingBook.setAuthor(updatedBookData.getAuthor());
            existingBook.setPrice(updatedBookData.getPrice());
            existingBook.setPublishedYear(updatedBookData.getPublishedYear());
            return bookRepository.save(existingBook);
        } else {
            throw new ResourceNotFoundException("Book with an ID " + updatedBookData.getId() + " is not found");
        }
    }

    private Specification<Book> getSpecification(Map<String, Object> filters) {
        return specificationBuilder.buildSpecification(filters);
    }
}