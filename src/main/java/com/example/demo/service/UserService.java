package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryService historyService;

    // 1. Tìm User theo Username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 2. Kiểm tra đăng nhập
    public User checkLogin(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            historyService.logAction("LOGIN", "AUTH", user.getUsername(), user.getUsername(), "Đăng nhập hệ thống thành công");
            return user;
        }
        return null;
    }

    // 3. Lấy danh sách toàn bộ User
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 4. Tạo mới - POST (Có check trùng Username)
    public User createUser(User newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new RuntimeException("user.username.exists");
        }
        if (newUser.getEmail() != null && userRepository.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("user.email.exists");
        }
        if (newUser.getPhone() != null && userRepository.existsByPhone(newUser.getPhone())) {
            throw new RuntimeException("user.phone.exists");
        }

        User savedUser = userRepository.save(newUser);

        historyService.logAction("CREATE", "USER", savedUser.getUsername(), "admin", "Tạo tài khoản: " + savedUser.getUsername());

        return savedUser;
    }

    // 5. Cập nhật - PUT (Dùng cho cả sửa Profile và Quên mật khẩu)
    public User updateUser(Long id, User updatedInfo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound", id));

        if (updatedInfo.getUsername() != null && !updatedInfo.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(updatedInfo.getUsername(), id)) {
                throw new RuntimeException("user.username.exists");
            }
            existingUser.setUsername(updatedInfo.getUsername());
        }

        if (updatedInfo.getPassword() != null && !updatedInfo.getPassword().isEmpty()) {
            existingUser.setPassword(updatedInfo.getPassword());
        }

        if (updatedInfo.getName() != null) existingUser.setName(updatedInfo.getName());
        if (updatedInfo.getPhone() != null) existingUser.setPhone(updatedInfo.getPhone());
        if (updatedInfo.getEmail() != null) existingUser.setEmail(updatedInfo.getEmail());

        User savedUser = userRepository.save(existingUser);

        historyService.logAction("UPDATE", "USER", savedUser.getUsername(), "admin", "Cập nhật hồ sơ: " + savedUser.getUsername());

        return savedUser;
    }

    // 6. Xóa User
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound", id));
        userRepository.deleteById(id);
        historyService.logAction("DELETE", "USER", user.getUsername(), "admin", "Xóa tài khoản: " + user.getUsername());
    }
}