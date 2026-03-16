package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.exception.ResourceNotFoundException;
import java.util.*;


@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookRepository bookRepository;
    @InjectMocks private BookService bookService;

    @Test void findAll_Ordered() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(new Book(), new Book()));
        assertEquals(2, bookService.findAllByOrderByTitleAsc().size());
    }
    @Test void findById_Success() {
        Book b = new Book(); b.setId(1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(b));
        assertEquals(1, bookService.findBookById(1).getId());
    }
    @Test void findById_Fail() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.findBookById(1));
    }
    @Test void save_New_Success() {
        Book b = new Book(); b.setTitle("Java");
        when(bookRepository.existsByTitle("Java")).thenReturn(false);
        when(bookRepository.save(b)).thenReturn(b);
        assertNotNull(bookService.saveBook(b));
    }
    @Test void save_New_TitleExists() {
        Book b = new Book(); b.setTitle("Java");
        when(bookRepository.existsByTitle("Java")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> bookService.saveBook(b));
    }
    @Test void save_Update_Success() {
        Book b = new Book(); b.setId(1); b.setTitle("Java");
        when(bookRepository.existsByTitleAndIdNot("Java", 1)).thenReturn(false);
        when(bookRepository.save(b)).thenReturn(b);
        assertNotNull(bookService.saveBook(b));
    }
    @Test void save_Update_TitleExists() {
        Book b = new Book(); b.setId(1); b.setTitle("Java");
        when(bookRepository.existsByTitleAndIdNot("Java", 1)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> bookService.saveBook(b));
    }
    @Test void delete_Success() {
        bookService.deleteBookById(1);
        verify(bookRepository).deleteById(1);
    }
    @Test void save_NullId_Check() {
        Book b = new Book(); b.setTitle("New");
        when(bookRepository.existsByTitle("New")).thenReturn(false);
        bookService.saveBook(b);
        verify(bookRepository).existsByTitle("New");
    }
    @Test void findAll_Empty() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        assertEquals(0, bookService.findAllByOrderByTitleAsc().size());
    }
}