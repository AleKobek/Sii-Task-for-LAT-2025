package edu.pja.sii_lat.repository;

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
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    Event event;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void init(){
        event = Event.builder()
                .name("TEST")
                .build();
    }

    @Test
    public void testRequiredDependencies(){
        assertNotNull(eventRepository);
    }

    @Test
    public void testSaveAll(){
        long beforeSave = eventRepository.count();
        eventRepository.save(event);
        entityManager.flush();
        long afterSave = eventRepository.count();
        assertEquals(++beforeSave, afterSave);
    }

    @Test
    public void testSaveEventWithInvalidName(){
        assertThrows(ConstraintViolationException.class, ()->{
           event.setName("");
           eventRepository.save(event);
           entityManager.flush();
        });
    }

    @Test
    public void testSaveEventWithInvalidFundsCurrencyCode(){
        assertThrows(ConstraintViolationException.class, ()->{
            event.setFundsCurrencyCode("");
            eventRepository.save(event);
            entityManager.flush();
        });
    }

    @Test
    public void testSaveEventWithInvalidFundsNumber(){
        assertThrows(ConstraintViolationException.class, ()->{
            event.setFunds(-1);
            eventRepository.save(event);
            entityManager.flush();
        });
    }
}
