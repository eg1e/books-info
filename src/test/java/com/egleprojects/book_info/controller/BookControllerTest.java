package com.egleprojects.book_info.controller;

import com.egleprojects.book_info.model.Book;
import com.egleprojects.book_info.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Book book;

    @BeforeEach
    void setUp() {
        System.out.println("b4test");
        book = new Book(null, "Romeo and Juliet", "William Shakespeare", 1597,
                null, 1299, 281);
        Book book1 = new Book(null, "Hamlet", "William Shakespeare", 1601,
                null, 1499, 289);
        bookRepository.saveAll(List.of(book, book1));
    }

    @AfterEach
    void tearDown(){
        bookRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE book_seq RESTART WITH 1");
    }

    @Test
    void getBook_AllBooks() throws Exception{
        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Romeo and Juliet"))
                .andExpect(jsonPath("$[0].author").value("William Shakespeare"))
                .andExpect(jsonPath("$[0].price").value(1299))
                .andExpect(jsonPath("$[1].title").value("Hamlet"))
                .andExpect(jsonPath("$[1].author").value("William Shakespeare"))
                .andExpect(jsonPath("$[1].price").value(1499));
    }

    @Test
    void addBook_Success() throws Exception {
        Book newBook = new Book(null, "The Symposium", "Plato", 381,
                null, 499, 90);
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Symposium"))
                .andExpect(jsonPath("$.author").value("Plato"))
                .andExpect(jsonPath("$.price").value(499));
    }

    @Test
    void updateBook_Success() throws Exception {
        book.setPrice(2199);

        mockMvc.perform(put("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Romeo and Juliet"))
                .andExpect(jsonPath("$.author").value("William Shakespeare"))
                .andExpect(jsonPath("$.price").value(2199));
    }

    @Test
    void updateBook_BookNotFound() throws Exception {
        Book updatedBook = new Book(55L,"The Odyssey", "Homer", 701,
                null, 1099, 541);

        System.out.println("hello: " + bookRepository.findAll());

        mockMvc.perform(put("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooks_WhereAuthorFilterMatches() throws Exception{
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                .param("author", "Homer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("The Odyssey"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBooks_WhereAuthorFilterDoesNotMatch() throws Exception{
        mockMvc.perform(get("/books")
                        .param("author", "Homer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WhereTitleFilterMatches() throws Exception{
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        Book newBook1 = new Book(null, "The Stranger", "Albert Camus", 1942,
                null, 1299, 123);
        Book newBook2 = new Book(null, "The Odyssey", "Lara Williams", 2022,
                null, 1399, 240);
        bookRepository.saveAll(List.of(newBook, newBook1, newBook2));

        mockMvc.perform(get("/books")
                        .param("title", "the Odyssey")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Homer"))
                .andExpect(jsonPath("$[1].author").value("Lara Williams"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBooks_WhereTitleFilterDoesNotMatch() throws Exception{
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        Book newBook1 = new Book(null, "The Stranger", "Albert Camus", 1942,
                null, 1299, 123);
        Book newBook2 = new Book(null, "The Odyssey", "Lara Williams", 2022,
                null, 1399, 240);
        bookRepository.saveAll(List.of(newBook, newBook1, newBook2));

        mockMvc.perform(get("/books")
                        .param("title", "1984")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WherePublishedYearMatches() throws Exception{
        mockMvc.perform(get("/books")
                        .param("publishedYear", "1601")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Hamlet"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBooks_WherePublishedYearDoesNotMatch() throws Exception{
        mockMvc.perform(get("/books")
                        .param("publishedYear", "1602")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WherePriceMatches() throws Exception{
        mockMvc.perform(get("/books")
                        .param("price", "1299")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Romeo and Juliet"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBooks_WherePriceDoesNotMatch() throws Exception{
        mockMvc.perform(get("/books")
                        .param("price", "1298")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WhereMinPriceMatches() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                .param("minprice", "1300")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Hamlet"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBooks_WhereMaxPriceMatches() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                        .param("maxprice", "1300")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Romeo and Juliet"))
                .andExpect(jsonPath("$[1].title").value("The Odyssey"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBooks_WhereBetweenPriceMatches() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                        .param("betweenprice", "1100,1400")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Romeo and Juliet"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBooks_WhereMinPriceMatchesZeroBooks() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                        .param("minprice", "1500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WhereMaxPriceMatchesZeroBooks() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                        .param("maxprice", "1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WhereBetweenPriceMatchesZeroBooks() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);

        mockMvc.perform(get("/books")
                        .param("betweenprice", "1500,2000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WhereMinUsedOnStringValue() throws Exception {
        Book newBook = new Book(null, "Lord of the Flies", "William Golding", 1954,
                null, 999, 182);
        bookRepository.save(newBook);
        mockMvc.perform(get("/books")
                        .param("minauthor", "William")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("William Shakespeare"))
                .andExpect(jsonPath("$[1].author").value("William Shakespeare"))
                .andExpect(jsonPath("$[2].author").value("William Golding"))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getBooks_WhereMaxUsedOnStringValue() throws Exception {

        mockMvc.perform(get("/books")
                        .param("maxauthor", "William")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBooks_WhereBetweenUsedOnStringValue() throws Exception {
        Book newBook = new Book(null, "The Odyssey", "Homer", 701,
                null, 1099, 541);
        bookRepository.save(newBook);
        mockMvc.perform(get("/books")
                        .param("betweenauthor", "M,Z")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}