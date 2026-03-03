package com.example.demo.service;

import java.util.*;
import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public  List<Book> findAll(){
        return bookRepository.findAll();
    }
}
