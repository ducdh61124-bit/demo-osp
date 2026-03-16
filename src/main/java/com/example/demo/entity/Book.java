package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 1000)
    private String title;

    private String author;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image;

    private Double price;

    private Long stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("books")
    private Category category;
}