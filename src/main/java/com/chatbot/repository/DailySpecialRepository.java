package com.chatbot.repository;

import com.chatbot.model.DailySpecial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DailySpecialRepository extends JpaRepository<DailySpecial, Long> {
    List<DailySpecial> findByDate(String date);
}