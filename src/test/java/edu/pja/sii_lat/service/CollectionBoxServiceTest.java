package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.*;
import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import edu.pja.sii_lat.repository.CollectionBoxRepository;
import edu.pja.sii_lat.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * mocks suddenly stopped working (functions return empty collection or null), so I'm testing manually
 */
@ExtendWith(MockitoExtension.class)
class CollectionBoxServiceTest {

    @Mock
    private ExchangeRateService exchangeRateService;
    @Mock
    private EventService eventService;
    @Mock
    private CollectionBoxRepository collectionBoxRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private CollectionBoxService collectionBoxService;

    Event event;

    CollectionBox collectionBox;

    Set<String> fakeValidCurrencies;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
        event.setId(1);
        event.setName("TEST");
        event.setFundsCurrencyCode("PLN");
        event.setFunds(1000);

        collectionBox = new CollectionBox();
        collectionBox.setEvent(event);
        event.getCollectionBoxSet().add(collectionBox);
        collectionBox.addFunds("PLN", 200);

        fakeValidCurrencies = new HashSet<>();
        fakeValidCurrencies.add("PLN");
        fakeValidCurrencies.add("USD");
    }

    @Test
    public void testRequiredDependencies(){
        assertNotNull(exchangeRateService);
        assertNotNull(eventService);
        assertNotNull(collectionBoxRepository);
        assertNotNull(collectionBoxService);
    }

    @Test
    void registerCollectionBox() {
        // Arrange
        Map<String, Double> funds = new HashMap<>();
            // the plan
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);

        // Act
        CreateCollectionBoxRes res = collectionBoxService.registerCollectionBox();

        // Assert
        assertNotNull(res);
        assertEquals(1, res.getId());
        assertEquals(funds, res.getFunds());
    }

    @Test
    void listCollectionBoxes() {
        // Arrange
            // fake response
        List<CollectionBox> list = Collections.singletonList(collectionBox);
            // the plan
        when(collectionBoxRepository.findAll()).thenReturn(list);

        // Act
        List<ListCollectionBoxResponse> res = collectionBoxService.listCollectionBoxes();

        // Assert
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getId());
        assertTrue(res.get(0).isAssigned());
        assertFalse(res.get(0).isEmpty());
    }

    @Test
    void unregisterCollectionBox() {
        // Arrange
            // the plan
        when(collectionBoxRepository.findById(1)).thenReturn(Optional.of(collectionBox));

        // Act
        collectionBoxService.unregisterCollectionBox(1);

        // Assert
        assertNull(collectionBox.getEvent());
        assertFalse(event.getCollectionBoxSet().contains(collectionBox));
    }

    @Test
    void testUnregisterCollectionBoxNotAssigned() {
        // Arrange
            // prepare collection box
        collectionBox.setEvent(null);
            // the plan
        when(collectionBoxRepository.findById(1)).thenReturn(Optional.of(collectionBox));

        // Act and Assert
        assertThrows(RuntimeException.class, () ->{
            collectionBoxService.unregisterCollectionBox(1);
        }, "Box with id = 1 is not registered to an event");
    }

    @Test
    void testAssignCollectionBox() {
        // Arrange
            // fake request
        AssignCollectionBoxReq req = new AssignCollectionBoxReq(1,1);
            // the plan
        when(collectionBoxRepository.findById(1)).thenReturn(Optional.of(collectionBox));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        BasicCollectionBoxDTO res = collectionBoxService.assignCollectionBox(req);

        // Assert
        assertNotNull(res);
        assertEquals(collectionBox.getId(), res.getId());
        assertEquals(event.getId(), res.getIdEvent());
    }

    @Test
    void depositMoney() {
        // Arrange
            // fake request
        DepositMoneyReq req = new DepositMoneyReq(1, 200, "USD");

            // the plan
        when(collectionBoxRepository.findById(1)).thenReturn(Optional.of(collectionBox));
        when(exchangeRateService.getValidCurrencies()).thenReturn(fakeValidCurrencies);
        when(exchangeRateService.getRateFor("USD","PLN")).thenReturn(3.5);
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);

        // Act
        BasicCollectionBoxDTO res = collectionBoxService.depositMoney(req);

        // Assert
        assertNotNull(res);
        assertEquals(collectionBox.getId(), res.getId());
    }

    @Test
    void emptyCollectionBox() {
        // Arrange
            // the plan
        when(collectionBoxRepository.findById(1)).thenReturn(Optional.of(collectionBox));
        when(exchangeRateService.getValidCurrencies()).thenReturn(fakeValidCurrencies);
        when(exchangeRateService.getRateFor("USD","PLN")).thenReturn(3.5);
        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        EmptyCollectionBoxRes res = collectionBoxService.emptyCollectionBox(collectionBox.getId());

        // Assert
        assertNotNull(res);
        assertEquals(collectionBox.getId(), res.getBoxId());
        assertEquals(event.getId(), res.getEventId());
        assertEquals(event.getFunds() + collectionBox.getFunds().get("USD") * 3.5,
                res.getEventFunds());
    }
}