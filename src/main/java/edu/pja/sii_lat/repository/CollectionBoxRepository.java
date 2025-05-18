package edu.pja.sii_lat.repository;

import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Integer> {

    public List<CollectionBox> findById(int id);

    public List<CollectionBox> findByEvent(Event event);

//    @Query("SELECT Count(collection_box_id) FROM collection_box_funds " +
//            "INNER JOIN CollectionBox ON CollectionBox.id = collection_box_funds.collection_box_id " +
//            "WHERE Funds > 0")
//    public int countNotEmptyCurrencies();
}
