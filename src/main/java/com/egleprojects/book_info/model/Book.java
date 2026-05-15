package com.egleprojects.book_info.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "book")

public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 100, message = "Author must be between 1 and 100 characters")
    private String author;
    
    @Min(value = 1000, message = "Published year must be valid")
    @Max(value = 2100, message = "Published year cannot be in the future")
    private int publishedYear;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookRating> ratings;
    
    @Min(value = 0, message = "Price cannot be negative")
    private Integer price;
    
    @Min(value = 1, message = "Pages must be at least 1")
    private int pages;

    public Book(Long id, String title, String author, int publishedYear, List<BookRating> ratings, Integer price, int pages) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.ratings = ratings;
        this.price = price;
        this.pages = pages;
    }

    public Book() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long bookId) {
        this.id = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String bookTitle) {
        this.title = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String bookAuthor) {
        this.author = bookAuthor;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int bookYear) {
        this.publishedYear = bookYear;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer bookPrice) {
        this.price = bookPrice;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}