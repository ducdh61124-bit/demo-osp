package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String phone;
    private String email;
    private String username;

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}