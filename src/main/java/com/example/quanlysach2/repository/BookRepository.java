package com.example.quanlysach2.repository;

import com.example.quanlysach2.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Page<Book> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Book> findByCategory(String category, Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCaseAndCategory(String keyword, String category, Pageable pageable);

    List<Book> findDistinctByOrderByCategoryAsc();
}