package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.entity.User;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import com.example.demo.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private UserService userService;

    @Test void findByUsername_Found() {
        User u = new User(); u.setUsername("abc");
        when(userRepository.findByUsername("abc")).thenReturn(Optional.of(u));
        assertEquals("abc", userService.findByUsername("abc").getUsername());
    }
    @Test void checkLogin_Success() {
        User u = new User(); u.setUsername("a"); u.setPassword("1");
        when(userRepository.findByUsername("a")).thenReturn(Optional.of(u));
        assertNotNull(userService.checkLogin("a", "1"));
    }
    @Test void checkLogin_Fail() {
        User u = new User(); u.setUsername("a"); u.setPassword("1");
        when(userRepository.findByUsername("a")).thenReturn(Optional.of(u));
        assertNull(userService.checkLogin("a", "wrong"));
    }
    @Test void create_UsernameExists() {
        User u = new User(); u.setUsername("admin");
        when(userRepository.existsByUsername("admin")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.createUser(u));
    }
    @Test void create_EmailExists() {
        User u = new User(); u.setEmail("a@g.com");
        when(userRepository.existsByEmail("a@g.com")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.createUser(u));
    }
    @Test void create_PhoneExists() {
        User u = new User(); u.setPhone("123");
        when(userRepository.existsByPhone("123")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.createUser(u));
    }
    @Test void update_UserNotFound() {
        UserUpdateDTO dto = new UserUpdateDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, dto));
    }
    @Test void update_DuplicateUsername() {
        User existing = new User(); existing.setId(1L); existing.setUsername("old");
        UserUpdateDTO info = new UserUpdateDTO(); info.setUsername("new");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsernameAndIdNot("new", 1L)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, info));
    }
    @Test void delete_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }
    @Test void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User()));
        assertEquals(1, userService.getAllUsers().size());
    }
}