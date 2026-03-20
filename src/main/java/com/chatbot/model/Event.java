package com.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date; // YYYY-MM-DD

    @Column(nullable = false)
    private String title; // e.g. "Live Jazz Night"

    @Column(nullable = false)
    private String description; // e.g. "Live jazz by Jimmy from 7pm-10pm"

    @Column
    private String startTime; // e.g. "7:00 PM"

    @Column
    private String endTime;   // e.g. "10:00 PM"
}
