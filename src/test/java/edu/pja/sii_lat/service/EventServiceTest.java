package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.CreateEventReq;
import edu.pja.sii_lat.DTO.CreateEventRes;
import edu.pja.sii_lat.DTO.FinancialReportRes;
import edu.pja.sii_lat.model.Event;
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
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private IExchangeRateService exchangeRateService;

    @InjectMocks
    private EventService eventService;

    Event event;

    Set<String> fakeValidCurrencies;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
        event.setId(1);
        event.setName("TEST");
        event.setFundsCurrencyCode("PLN");
        event.setFunds(1000);

        fakeValidCurrencies = new HashSet<>();
        fakeValidCurrencies.add("PLN");
    }

    @Test
    public void testRequiredDependencies(){
        assertNotNull(exchangeRateService);
        assertNotNull(eventService);
    }

    @Test
    void testCreateEvent() {
        // Arrange
            // fake request
        CreateEventReq request = new CreateEventReq("TEST", "PLN");

            // fake responses = event and fake valid currencies
        Set<String> fakeValidCurrencies = new HashSet<>();
        fakeValidCurrencies.add("PLN");
            // the plan
        when(exchangeRateService.getValidCurrencies()).thenReturn(
                fakeValidCurrencies);
        when(eventRepository.save(any(Event.class))).thenReturn(event);



        // Act
        CreateEventRes response = eventService.createEvent(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("TEST", response.getName());
        assertEquals("PLN", response.getFundsCurrencyCode());
    }

    @Test
    void testCreateEventInvalidCurrency() {
        // Arrange
            // fake request
        CreateEventReq request = new CreateEventReq("TEST", "SII");
            // fake response
        Set<String> temp = new HashSet<>();
            // the plan
        when(exchangeRateService.getValidCurrencies()).thenReturn(temp);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            eventService.createEvent(request);
        }, "Currency code SII is not supported");
    }

    @Test
    void testCreateEventEmptyName(){
        // Arrange
            // fake request
        CreateEventReq request = new CreateEventReq("", "PLN");

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () ->{
            eventService.createEvent(request);
        }, "Event's name cannot be empty");
    }


    @Test
    void testGenerateFinancialReport() {
        // Arrange
            // fake response = event

            // the plan
        when(eventRepository.findAll()).thenReturn(List.of(event));

        // Act
        List<FinancialReportRes> report = eventService.generateFinancialReport();

        // Assert
        assertNotNull(report);
        assertEquals(1, report.size());
        assertEquals("TEST", report.get(0).getName());
        assertEquals("PLN", report.get(0).getFundsCurrencyCode());
        assertEquals(1000, report.get(0).getFunds());
    }


}