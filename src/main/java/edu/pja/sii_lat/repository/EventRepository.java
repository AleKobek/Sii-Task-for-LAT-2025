package edu.pja.sii_lat.repository;

import edu.pja.sii_lat.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Integer> {

}
