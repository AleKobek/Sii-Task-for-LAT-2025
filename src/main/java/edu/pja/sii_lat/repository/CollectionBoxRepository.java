package edu.pja.sii_lat.repository;

import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollectionBoxRepository extends CrudRepository<CollectionBox, Integer> {

    public List<CollectionBox> findById(int id);

    public List<CollectionBox> findByEvent(Event event);
}
