package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.*;

@Entity
@Table(name = "Books")

public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 1000)
    private String title;
    private String author;
    @Column(length = 1000)
    private String image;
    private Double price;
    private Long stock;

    public Book() {}

    public Book(Integer id, String title, String author, String image, Double price, Long stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.image = image;
        this.price = price;
        this.stock = stock;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Double getPrice() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
