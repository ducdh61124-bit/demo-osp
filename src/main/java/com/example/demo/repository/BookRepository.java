package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Integer id);
}