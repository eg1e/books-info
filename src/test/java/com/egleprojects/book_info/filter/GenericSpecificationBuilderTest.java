package com.egleprojects.book_info.filter;

import com.egleprojects.book_info.model.Book;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericSpecificationBuilderTest {
    @Mock
    private Root<Book> root;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @Mock
    private Predicate conjunctionPredicate;
    @Mock
    private Predicate andPredicate;
    @InjectMocks
    private GenericSpecificationBuilder<Book> specificationBuilder;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        specificationBuilder = new GenericSpecificationBuilder<>();
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void buildSpecification_WhenUsingMinFilter(){
        Map<String, Object> filters = new HashMap<>();
        filters.put("minprice", 1500);

        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(conjunctionPredicate, eq(any()))).thenReturn(andPredicate);

        var spec = specificationBuilder.buildSpecification(filters);
        var predicate = spec.toPredicate(root, null, criteriaBuilder);

        assertNotNull(predicate);

        verify(criteriaBuilder, times(1)).conjunction();
        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(any(), eq("1500"));
        verify(criteriaBuilder, times(1)).and(conjunctionPredicate, eq(any()));
        verify(root, times(1)).get("price");
    }

    @Test
    void buildSpecification_WhenUsingMaxFilter(){
        Map<String, Object> filters = new HashMap<>();
        filters.put("maxprice", 1500);

        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(conjunctionPredicate, eq(any()))).thenReturn(andPredicate);

        var spec = specificationBuilder.buildSpecification(filters);
        var predicate = spec.toPredicate(root, null, criteriaBuilder);

        assertNotNull(predicate);

        verify(criteriaBuilder, times(1)).conjunction();
        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(any(), eq("1500"));
        verify(criteriaBuilder, times(1)).and(conjunctionPredicate, eq(any()));
        verify(root, times(1)).get("price");
    }

    @Test
    void buildSpecification_WhenUsingBetweenFilter(){
        Map<String, Object> filters = new HashMap<>();
        filters.put("betweenprice", "1500,2000");

        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(conjunctionPredicate, eq(any()))).thenReturn(andPredicate);

        var spec = specificationBuilder.buildSpecification(filters);
        var predicate = spec.toPredicate(root, null, criteriaBuilder);

        assertNotNull(predicate);

        verify(criteriaBuilder, times(1)).conjunction();
        verify(criteriaBuilder, times(1)).between(any(), eq("1500"), eq("2000"));
        verify(criteriaBuilder, times(1)).and(conjunctionPredicate, eq(any()));
        verify(root, times(1)).get("price");
    }

    @Test
    void buildSpecification_WhenUsingStringFilter(){
        Map<String, Object> filters = new HashMap<>();
        filters.put("author", "William Shakespeare");

        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(conjunctionPredicate, eq(any()))).thenReturn(andPredicate);

        var spec = specificationBuilder.buildSpecification(filters);
        var predicate = spec.toPredicate(root, null, criteriaBuilder);

        assertNotNull(predicate);

        verify(criteriaBuilder, times(1)).conjunction();
        verify(criteriaBuilder, times(1)).and(conjunctionPredicate, eq(any()));
        verify(criteriaBuilder, times(1)).like(any(), eq("%William Shakespeare%"));
        verify(root, times(1)).get("author");
    }

    @Test
    void buildSpecification_WhenUsingNonStringFilter(){
        Map<String, Object> filters = new HashMap<>();
        filters.put("pages", 222);

        when(criteriaBuilder.conjunction()).thenReturn(conjunctionPredicate);
        when(criteriaBuilder.and(conjunctionPredicate, eq(any()))).thenReturn(andPredicate);

        var spec = specificationBuilder.buildSpecification(filters);
        var predicate = spec.toPredicate(root, null, criteriaBuilder);

        assertNotNull(predicate);

        verify(criteriaBuilder, times(1)).conjunction();
        verify(criteriaBuilder, times(1)).and(conjunctionPredicate, eq(any()));
        verify(criteriaBuilder, times(1)).equal(any(), eq(222));
        verify(root, times(1)).get("pages");
    }
}