package com.chatbot.controller;

import com.chatbot.model.Event;
import com.chatbot.model.Offer;
import com.chatbot.service.EventOfferService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventOfferService eventOfferService;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:changeme123}")
    private String adminPassword;

    // ── LOGIN ──────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> credentials,
            HttpSession session) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            response.put("success", true);
            response.put("message", "Login successful");
            log.info("Admin logged in successfully");
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            log.warn("Failed admin login attempt");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out");
        return ResponseEntity.ok(response);
    }

    // ── OFFERS ─────────────────────────────────────────────────────────────

    @GetMapping("/offers")
    public ResponseEntity<?> getOffers(HttpSession session) {
        if (!isLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(eventOfferService.getAllOffers());
    }

    @PostMapping("/offers")
    public ResponseEntity<?> addOffer(
            @RequestBody Offer offer,
            HttpSession session) {
        if (!isLoggedIn(session)) return unauthorized();
        Offer saved = eventOfferService.saveOffer(offer);
        log.info("Admin added offer: {}", saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<?> deleteOffer(
            @PathVariable Long id,
            HttpSession session) {
        if (!isLoggedIn(session)) return unauthorized();
        eventOfferService.deleteOffer(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Offer deleted");
        return ResponseEntity.ok(response);
    }

    // ── EVENTS ─────────────────────────────────────────────────────────────

    @GetMapping("/events")
    public ResponseEntity<?> getEvents(HttpSession session) {
        if (!isLoggedIn(session)) return unauthorized();
        return ResponseEntity.ok(eventOfferService.getAllEvents());
    }

    @PostMapping("/events")
    public ResponseEntity<?> addEvent(
            @RequestBody Event event,
            HttpSession session) {
        if (!isLoggedIn(session)) return unauthorized();
        Event saved = eventOfferService.saveEvent(event);
        log.info("Admin added event: {}", saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id,
            HttpSession session) {
        if (!isLoggedIn(session)) return unauthorized();
        eventOfferService.deleteEvent(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Event deleted");
        return ResponseEntity.ok(response);
    }

    // ── HELPERS ────────────────────────────────────────────────────────────

    private boolean isLoggedIn(HttpSession session) {
        Object loggedIn = session.getAttribute("adminLoggedIn");
        return Boolean.TRUE.equals(loggedIn);
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Unauthorized — please log in");
        return ResponseEntity.status(401).body(response);
    }
}
