package edu.pja.sii_lat.repository;

import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CollectionBoxRepositoryTest {

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

    CollectionBox collectionBox;


    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void init(){
        Event event = Event.builder()
                .name("TEST")
                .build();
        collectionBox = CollectionBox.builder()
                .event(event)
                .build();
        collectionBox.addFunds("EUR", 300);
    }

    @Test
    public void testRequiredDependencies(){
        assertNotNull(collectionBoxRepository);
    }

    @Test
    public void testSaveAll(){
        long beforeSave = collectionBoxRepository.count();
        collectionBoxRepository.save(collectionBox);
        entityManager.flush();
        long afterSave = collectionBoxRepository.count();
        assertEquals(++beforeSave, afterSave);
    }

    @Test
    public void testSaveCollectionBoxWithInvalidEvent(){
        assertThrows(ConstraintViolationException.class, ()->{
            collectionBox.setEvent(null);
            collectionBoxRepository.save(collectionBox);
            entityManager.flush();
        });
    }

}
