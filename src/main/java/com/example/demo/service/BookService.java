package com.example.demo.service;

import java.util.*;
import com.example.demo.entity.Book;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    //Lấy danh sách Sách
    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    //Lưu Sách
    public Book saveBook(Book book){
        return bookRepository.save(book);
    }

    //Tìm Sách theo ID
    public Book findBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found id: " + id));
    }

    //Xóa Sách
    public void  deleteBookById(Integer id){
        bookRepository.deleteById(id);
    }

}
