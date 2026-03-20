package com.chatbot.repository;

import com.chatbot.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDate(String date);
    List<Event> findByDateGreaterThanEqualOrderByDateAsc(String date);
}
