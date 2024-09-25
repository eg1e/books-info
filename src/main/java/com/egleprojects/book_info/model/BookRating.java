package com.egleprojects.book_info.model;

import jakarta.persistence.*;

@Entity
public class BookRating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private int rating;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")

    private Book book;

    public BookRating(Long Id, int rating, Book book) {
        this.Id = Id;
        this.rating = rating;
        this.book = book;
    }

    public BookRating() {
    }

    public Long getId() {
        return Id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int ratingValue) {
        this.rating = ratingValue;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
