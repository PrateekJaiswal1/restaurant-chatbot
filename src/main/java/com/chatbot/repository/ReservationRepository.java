package com.chatbot.repository;

import com.chatbot.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COALESCE(SUM(r.partySize), 0) FROM Reservation r " +
           "WHERE r.date = :date AND r.time = :time AND r.status = 'CONFIRMED'")
    int countBookedSeats(@Param("date") String date, @Param("time") String time);
}
