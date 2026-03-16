package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {

        System.out.println("Đang gửi Email tới: " + to + " với mã OTP: " + otp);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("MÃ XÁC NHẬN QUÊN MẬT KHẨU - SMARTBOOK");
        message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otp +
                "\n\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ cho ai!");

        mailSender.send(message);
        System.out.println("Đã gửi mail thành công!");
    }
}