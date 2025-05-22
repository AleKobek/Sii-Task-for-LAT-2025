package edu.pja.sii_lat.repository;

import edu.pja.sii_lat.model.CollectionBox;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        collectionBox = CollectionBox.builder().build();
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

}
