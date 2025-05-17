package edu.pja.sii_lat.repository;

import edu.pja.sii_lat.model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Integer> {

    public List<Event> findById(int id);

    public List<Event> findByName(String name);
}
