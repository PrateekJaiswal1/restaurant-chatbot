package com.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "daily_special")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySpecial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date;        // e.g. "2026-03-21"

    @Column(nullable = false)
    private String itemName;

    @Column
    private String description;

    @Column(nullable = false)
    private double price;
}
