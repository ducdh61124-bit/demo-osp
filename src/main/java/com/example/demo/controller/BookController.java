package com.example.demo.controller;

import com.example.demo.configuration.BookstoreAppPropertiesConfiguration;
import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import com.example.demo.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private BookstoreAppPropertiesConfiguration appProperties;

    // 1. Lấy tất cả danh sách sách - GET
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    // 2. Lấy chi tiết - GET
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Integer id) {
        return bookService.findBookById(id);
    }

    // 3. Lấy thông tin hệ thống - GET
    @GetMapping("/system-info")
    public ResponseEntity<ApiResponse> getSystemInfo() {
        ApiResponse response = new ApiResponse(
                200,
                "Lấy thông tin hệ thống thành công",
                appProperties,
                null
        );
        return ResponseEntity.ok(response);
    }

    // 4. Thêm mới sách - POST
    @PostMapping
    public ResponseEntity<String> saveBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        String msg = messageSource.getMessage("book.create.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg + ": " + savedBook.getTitle());
    }

    // 5. Sửa thông tin sách - PUT
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@RequestBody Book newInfo, @PathVariable Integer id) {
        Book bookCurrently = bookService.findBookById(id);

        if (newInfo.getAuthor() != null) bookCurrently.setAuthor(newInfo.getAuthor());
        if (newInfo.getPrice() != null) bookCurrently.setPrice(newInfo.getPrice());
        if (newInfo.getTitle() != null) bookCurrently.setTitle(newInfo.getTitle());
        if (newInfo.getImage() != null) bookCurrently.setImage(newInfo.getImage());
        if (newInfo.getStock() != null) bookCurrently.setStock(newInfo.getStock());

        bookService.saveBook(bookCurrently);
        return ResponseEntity.ok("Update success!");
    }

    // 6. Xóa sách - DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable Integer id) {
        bookService.findBookById(id);
        bookService.deleteBookById(id);
        String msg = messageSource.getMessage("book.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg);
    }
}