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

    @Autowired
    private HistoryService historyService;

    //Lấy danh sách Sách
    public List<Book> findAllByOrderByTitleAsc(){
        return bookRepository.findAll();
    }

    //Lưu Sách
    public Book saveBook(Book book){
        boolean isNew = (book.getId() == null);

        if (isNew) {
            if (bookRepository.existsByTitle(book.getTitle()))
                throw new RuntimeException("book.title.exists");
        } else {
            if (bookRepository.existsByTitleAndIdNot(book.getTitle(), book.getId()))
                throw new RuntimeException("book.title.exists");
        }

        Book savedBook = bookRepository.save(book);

        if (isNew) {
            historyService.logAction("CREATE", "BOOK", savedBook.getTitle(), "admin", "Thêm sách mới: " + savedBook.getTitle());
        } else {
            historyService.logAction("UPDATE", "BOOK", savedBook.getTitle(), "admin", "Cập nhật thông tin sách: " + savedBook.getTitle());
        }

        return savedBook;
    }

    //Tìm Sách theo ID
    public Book findBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("book.notfound", id));
    }

    //Xóa Sách
    public void  deleteBookById(Integer id){
        bookRepository.findById(id).ifPresent(book -> {
            bookRepository.deleteById(id);
            historyService.logAction("DELETE", "BOOK", book.getTitle(), "admin", "Xóa sách: " + book.getTitle());
        });
    }
}
