package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private MessageSource messageSource;

    // 1. Lấy tất cả danh sách sách - GET
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAllByOrderByTitleAsc();
    }

    // 2. Lấy chi tiết - GET
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Integer id) {
        return bookService.findBookById(id);
    }

    // 3. Thêm mới sách - POST
    @PostMapping
    public ResponseEntity<String> saveBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        String msg = messageSource.getMessage("book.create.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg + ": " + savedBook.getTitle());
    }

    // 4. Sửa thông tin sách - PUT
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@RequestBody Book newInfo, @PathVariable Integer id) {
        Book bookCurrently = bookService.findBookById(id);

        if (newInfo.getAuthor() != null) bookCurrently.setAuthor(newInfo.getAuthor());
        if (newInfo.getPrice() != null) bookCurrently.setPrice(newInfo.getPrice());
        if (newInfo.getTitle() != null) bookCurrently.setTitle(newInfo.getTitle());
        if (newInfo.getImage() != null) bookCurrently.setImage(newInfo.getImage());
        if (newInfo.getStock() != null) bookCurrently.setStock(newInfo.getStock());
        if (newInfo.getCategory() != null && newInfo.getCategory().getId() != null) {
            bookCurrently.setCategory(newInfo.getCategory());
        }

        bookService.saveBook(bookCurrently);
        String msg = messageSource.getMessage("book.update.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg);
    }

    // 5. Xóa sách - DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable Integer id) {
        bookService.findBookById(id);
        bookService.deleteBookById(id);
        String msg = messageSource.getMessage("book.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg);
    }
}