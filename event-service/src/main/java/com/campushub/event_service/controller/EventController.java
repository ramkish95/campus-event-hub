package com.campushub.event_service.controller;

import com.campushub.event_service.model.Event;
import com.campushub.event_service.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event, @AuthenticationPrincipal String username) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Event title is required!");
        }
        
        event.setOrganizedBy(username);
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }

    @GetMapping // 🌟 FIX: Capitalized 'G' here
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }
}