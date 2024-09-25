package com.egleprojects.book_info.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "book")

public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;
    private int publishedYear;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookRating> ratings;
    private Integer price;
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
}