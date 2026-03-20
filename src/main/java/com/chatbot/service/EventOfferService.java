package com.chatbot.service;

import com.chatbot.model.Event;
import com.chatbot.model.Offer;
import com.chatbot.repository.EventRepository;
import com.chatbot.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventOfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private EventRepository eventRepository;

    // Tool 5: Get today's offers
    public String getTodaysOffers() {
        String today = LocalDate.now().toString();
        List<Offer> offers = offerRepository.findByDate(today);

        if (offers.isEmpty()) {
            return "No special offers today — but our full menu is always amazing! " +
                   "Would you like to see today's specials instead?";
        }

        StringBuilder sb = new StringBuilder("🏷️ Today's special offers:\n\n");
        for (Offer offer : offers) {
            sb.append(String.format("• **%s** — %s", offer.getTitle(), offer.getDescription()));
            if (offer.getValidUntil() != null && !offer.getValidUntil().isEmpty()) {
                sb.append(String.format(" (valid until %s)", offer.getValidUntil()));
            }
            sb.append("\n");
        }
        sb.append("\nWould you like to make a reservation to take advantage of these offers?");
        return sb.toString();
    }

    // Tool 6: Get upcoming events
    public String getUpcomingEvents() {
        String today = LocalDate.now().toString();
        List<Event> events = eventRepository.findByDateGreaterThanEqualOrderByDateAsc(today);

        if (events.isEmpty()) {
            return "No upcoming events scheduled right now — check back soon! " +
                   "Would you like to make a regular reservation?";
        }

        StringBuilder sb = new StringBuilder("🎵 Upcoming events:\n\n");
        for (Event event : events) {
            sb.append(String.format("• **%s** on %s", event.getTitle(), event.getDate()));
            if (event.getStartTime() != null) {
                sb.append(String.format(" from %s", event.getStartTime()));
            }
            if (event.getEndTime() != null) {
                sb.append(String.format(" to %s", event.getEndTime()));
            }
            sb.append(String.format(" — %s\n", event.getDescription()));
        }
        sb.append("\nWould you like to book a table for any of these events?");
        return sb.toString();
    }

    // Admin methods - save offer
    public Offer saveOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    // Admin methods - save event
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    // Admin methods - delete offer
    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    // Admin methods - delete event
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Admin methods - get all offers from today onwards
    public List<Offer> getAllOffers() {
        return offerRepository.findByDate(LocalDate.now().toString());
    }

    // Admin methods - get all upcoming events
    public List<Event> getAllEvents() {
        return eventRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now().toString());
    }
}
