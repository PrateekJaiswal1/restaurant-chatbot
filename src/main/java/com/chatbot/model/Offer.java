package com.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "offer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date; // YYYY-MM-DD

    @Column(nullable = false)
    private String title; // e.g. "Happy Hour"

    @Column(nullable = false)
    private String description; // e.g. "20% off all pasta dishes 5pm-7pm"

    @Column
    private String validUntil; // e.g. "7:00 PM"
}
