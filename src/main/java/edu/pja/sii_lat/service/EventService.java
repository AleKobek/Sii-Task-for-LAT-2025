package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.CreateEventReq;
import edu.pja.sii_lat.DTO.CreateEventRes;
import edu.pja.sii_lat.DTO.FinancialReportRes;
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


    public CreateEventRes createEvent(CreateEventReq req){

        // validating parameters
        if(req.getName() == null ||req.getName().isBlank()){
            throw new IllegalArgumentException("Event's name cannot be empty");
        }
        if(req.getFundsCurrencyCode() == null || req.getFundsCurrencyCode().isBlank()){
            throw new IllegalArgumentException("Event's account currency cannot be empty");
        }

        if(exchangeRateService.getValidCurrencies().isEmpty()){
            // only to load valid currencies
            try {
                exchangeRateService.getRateFor("USD", "PLN");
            }catch (RuntimeException e){
                throw new RuntimeException("Cannot create event - error occurred while fetching supported currencies");
            }
        }
        if(!exchangeRateService.getValidCurrencies().contains(req.getFundsCurrencyCode())){
            throw new RuntimeException("Currency code " + req.getFundsCurrencyCode() + " is not supported");
        }
        // done validating, creating event
        Event event = new Event();
        event.setFunds(0);
        event.setName(req.getName());
        event.setFundsCurrencyCode(req.getFundsCurrencyCode());

        Event savedEvent = eventRepository.save(event);

        return new CreateEventRes(savedEvent.getId(), savedEvent.getName(), savedEvent.getFundsCurrencyCode());
    }

    public List<FinancialReportRes> generateFinancialReport(){
        List<Event> events = eventRepository.findAll().stream().toList();
        List<FinancialReportRes> finantialReportList = new ArrayList<>();
        for(Event event : events){
            finantialReportList.add(new FinancialReportRes(
                    event.getName(), event.getFundsCurrencyCode(), event.getFunds()
            ));
        }
        return finantialReportList;
    }
}