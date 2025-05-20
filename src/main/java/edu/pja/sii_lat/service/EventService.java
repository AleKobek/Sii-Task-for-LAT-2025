package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.CreateEventReq;
import edu.pja.sii_lat.DTO.CreateEventRes;
import edu.pja.sii_lat.DTO.EventAndAccountDTO;
import edu.pja.sii_lat.model.Event;
import edu.pja.sii_lat.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService{

    private final EventRepository eventRepository;


    private final IExchangeRateService exchangeRateService;


    // 1. Create a new fundraising event.
    public CreateEventRes createEvent(CreateEventReq req){
        if(req.getName() == null ||req.getName().isBlank()){
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        if(req.getFundsCurrencyCode() == null || req.getFundsCurrencyCode().isBlank()){
            throw new IllegalArgumentException("Event's account currency cannot be empty");
        }
        if(exchangeRateService.getValidCurrencies().isEmpty()){
            // only to get valid currencies
            try {
                exchangeRateService.getRateFor("USD", "PLN");
            }catch (RuntimeException e){
                throw new RuntimeException("Cannot create event - error occurred while fetching supported currencies");
            }
        }
        Event event = new Event();
        event.setFunds(0);
        event.setName(req.getName());
        event.setFundsCurrencyCode(req.getFundsCurrencyCode());

        Event savedEvent = eventRepository.save(event);

        return new CreateEventRes(savedEvent.getId(), savedEvent.getName(), savedEvent.getFundsCurrencyCode());
    }

    // 8. Display a financial report with all fundraising events and the sum of their accounts.
    public List<EventAndAccountDTO> financialReport(){
        List<Event> events = eventRepository.findAll().stream().toList();
        List<EventAndAccountDTO> finantialReportList = new ArrayList<>();
        for(Event event : events){
            finantialReportList.add(new EventAndAccountDTO(
                    event.getName(), event.getFundsCurrencyCode(), event.getFunds()
            ));
        }
        return finantialReportList;
    }
}

