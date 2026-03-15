package com.chatbot.service;

import com.chatbot.model.DailySpecial;
import com.chatbot.model.Reservation;
import com.chatbot.repository.DailySpecialRepository;
import com.chatbot.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Value("${restaurant.max-capacity:30}")
    private int maxCapacity;

    @Value("${restaurant.phone:(512) 555-0198}")
    private String restaurantPhone;

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private DailySpecialRepository specialsRepo;

    // -----------------------------------------------
    // Tool 1: Check availability
    // -----------------------------------------------
    public String checkAvailability(String date, String time, int partySize) {
        int bookedSeats = reservationRepo.countBookedSeats(date, time);
        int availableSeats = maxCapacity - bookedSeats;

        if (availableSeats >= partySize) {
            return String.format(
                "Great news! We have availability on %s at %s for %d guests. " +
                "%d seats are still available in that slot. Would you like to make a reservation?",
                date, time, partySize, availableSeats
            );
        } else if (availableSeats > 0) {
            return String.format(
                "We only have %d seats available on %s at %s, but you need %d. " +
                "Would you like to try a different time or reduce your party size?",
                availableSeats, date, time, partySize
            );
        } else {
            return String.format(
                "We are fully booked on %s at %s. " +
                "Would you like to try a different time or date?",
                date, time
            );
        }
    }

    // -----------------------------------------------
    // Tool 2: Make a reservation
    // -----------------------------------------------
    public String makeReservation(String customerName, String customerPhone,
                                   String date, String time, int partySize) {
        int bookedSeats = reservationRepo.countBookedSeats(date, time);
        int availableSeats = maxCapacity - bookedSeats;

        if (availableSeats < partySize) {
            return String.format(
                "Sorry, we no longer have enough space for %d guests on %s at %s. " +
                "Would you like to try another time?",
                partySize, date, time
            );
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerName(customerName);
        reservation.setCustomerPhone(customerPhone);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setPartySize(partySize);
        reservation.setStatus("CONFIRMED");

        Reservation saved = reservationRepo.save(reservation);

        return String.format(
            "Your reservation is confirmed! Booking ID: #%d. " +
            "Name: %s | Date: %s | Time: %s | Party of %d. " +
            "We look forward to seeing you! To cancel, use Booking ID #%d or call %s.",
            saved.getId(), customerName, date, time, partySize, saved.getId(), restaurantPhone
        );
    }

    // -----------------------------------------------
    // Tool 3: Cancel a reservation
    // -----------------------------------------------
    public String cancelReservation(Long reservationId) {
        Optional<Reservation> optional = reservationRepo.findById(reservationId);

        if (optional.isEmpty()) {
            return String.format(
                "Sorry, I couldn't find reservation #%d. " +
                "Please double-check your booking ID or call us at %s.",
                reservationId, restaurantPhone
            );
        }

        Reservation reservation = optional.get();

        if ("CANCELLED".equals(reservation.getStatus())) {
            return String.format("Reservation #%d is already cancelled.", reservationId);
        }

        reservation.setStatus("CANCELLED");
        reservationRepo.save(reservation);

        return String.format(
            "Reservation #%d for %s on %s at %s has been successfully cancelled. " +
            "We hope to see you another time!",
            reservationId, reservation.getCustomerName(),
            reservation.getDate(), reservation.getTime()
        );
    }

    // -----------------------------------------------
    // Tool 4: Get today's specials
    // -----------------------------------------------
    public String getTodaysSpecials() {
        String today = LocalDate.now().toString();
        List<DailySpecial> specials = specialsRepo.findByDate(today);

        if (specials.isEmpty()) {
            return "Today's specials are being prepared! Please ask your server when you arrive " +
                   "or call us at " + restaurantPhone + " for today's offerings.";
        }

        StringBuilder sb = new StringBuilder("Today's specials:\n\n");
        for (DailySpecial special : specials) {
            sb.append(String.format("• %s — %s ($%.2f)\n",
                special.getItemName(), special.getDescription(), special.getPrice()));
        }
        sb.append("\nWould you like to make a reservation to enjoy these tonight?");
        return sb.toString();
    }
}
