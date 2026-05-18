package com.campushub.event_service.repository;

import com.campushub.event_service.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    
    // Custom query method to easily fetch events hosted by a specific student group/club
    List<Event> findByOrganizedBy(String organizerUsername);
}