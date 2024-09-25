package com.egleprojects.book_info.repository;

import com.egleprojects.book_info.model.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<BookRating, Long> {
    List<BookRating> findBookRatingsByBookId(Long bookId);

    @Query("SELECT CASE WHEN COUNT(b) = 0 THEN 0 ELSE COALESCE(AVG(r.rating), 0) END " +
            "FROM Book b LEFT JOIN BookRating r ON b.id = r.book.id " +
            "WHERE b.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
}
