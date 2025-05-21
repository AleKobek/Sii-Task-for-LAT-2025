package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.*;
import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import edu.pja.sii_lat.repository.CollectionBoxRepository;
import edu.pja.sii_lat.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CollectionBoxService implements ICollectionBoxService{

    private final CollectionBoxRepository collectionBoxRepository;

    private final EventRepository eventRepository;

    private final IExchangeRateService exchangeRateService;



    public CreateCollectionBoxRes registerCollectionBox(){
        CollectionBox collectionBox = new CollectionBox();
        CollectionBox savedCollectionBox = collectionBoxRepository.save(collectionBox);
        return new CreateCollectionBoxRes(savedCollectionBox.getId(), savedCollectionBox.getFunds());
    }



    public List<ListCollectionBoxResponse> listCollectionBoxes(){
        List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll().stream().toList();
        List<ListCollectionBoxResponse> DTOsList = new ArrayList<>();
        for(CollectionBox collectionBox: collectionBoxes){
            DTOsList.add(new ListCollectionBoxResponse(
                    collectionBox.getId(), collectionBox.getEvent() == null, collectionBox.isEmpty()
            ));
        }
        return DTOsList;
    }


    public void unregisterCollectionBox(Integer boxId){
        CollectionBox collectionBox = fetchCollectionBox(boxId);
        if(collectionBox.getEvent() == null){
            throw new RuntimeException("Box with id = " + boxId + " is not registered to an event");
        }
        collectionBox.getEvent().getCollectionBoxSet().remove(collectionBox);
        collectionBox.getFunds().clear();
        collectionBox.setEvent(null);
        collectionBoxRepository.save(collectionBox);
        collectionBoxRepository.delete(collectionBox);
    }


    public BasicCollectionBoxDTO assignCollectionBox(AssignCollectionBoxReq req){
        Integer boxId = req.getIdBox();
        Integer eventId = req.getIdEvent();

        CollectionBox collectionBox = fetchCollectionBox(boxId);
        Event event = fetchEvent(eventId);

        if(collectionBox.getEvent() != null){
            throw new RuntimeException("Box with id = " + boxId + " is already assigned");
        }
        if(!collectionBox.isEmpty()){
            throw new RuntimeException("Box with id = " + boxId + " is not empty");
        }
        collectionBox.setEvent(event);
        event.getCollectionBoxSet().add(collectionBox);
        collectionBoxRepository.save(collectionBox);
        eventRepository.save(event);
        return new BasicCollectionBoxDTO(
                collectionBox.getId(), collectionBox.getEvent().getId(), collectionBox.getFunds()
        );
    }


    public BasicCollectionBoxDTO depositMoney(DepositMoneyReq req){
        Integer boxId = req.getBoxId();
        // validating parameters
        if(req.getAmount() == 0){
            throw new IllegalArgumentException("Amount to deposit cannot be zero");
        }
        if(exchangeRateService.getValidCurrencies().isEmpty()){
            // only to get valid currencies
            try {
                exchangeRateService.getRateFor("USD", "PLN");
            }catch (RuntimeException e){
                throw new RuntimeException("Cannot deposit money - error occurred while fetching supported currencies");
            }
        }
        if(!exchangeRateService.getValidCurrencies().contains(req.getFundsCurrencyCode())){
            throw new RuntimeException("Currency code " + req.getFundsCurrencyCode() + " is not supported");
        }
        CollectionBox collectionBox = fetchCollectionBox(boxId);
        // done validating, adding funds
        collectionBox.addFunds(req.getFundsCurrencyCode(), req.getAmount());
        CollectionBox savedCollectionBox = collectionBoxRepository.save(collectionBox);
        return new BasicCollectionBoxDTO(
                savedCollectionBox.getId(), savedCollectionBox.getEvent().getId(), savedCollectionBox.getFunds()
        );
    }


    public EmptyCollectionBoxRes emptyCollectionBox(Integer boxId){
        // validating parameters
        CollectionBox collectionBox = fetchCollectionBox(boxId);
        if(collectionBox.isEmpty()){
            throw new RuntimeException("Box with id = " + boxId + " is already empty");
        }
        Event event = collectionBox.getEvent();
        if(event == null){
            throw new RuntimeException("Box with id = " + boxId + " is not assigned to an event");
        }
        // done validating, emptying box
        double moneyToTransfer = 0;
        String targetCurrency = event.getFundsCurrencyCode();
        // processing each currency
        for(String currencyCode: collectionBox.getFunds().keySet()){
            double amountBeforeExchange = collectionBox.getFunds().get(currencyCode);
            // if it's the same currency
            if (currencyCode.equals(targetCurrency)) {
                moneyToTransfer += amountBeforeExchange;
            }
            else {
                double exchangeRate = exchangeRateService.getRateFor(currencyCode, targetCurrency);
                moneyToTransfer += amountBeforeExchange * exchangeRate;
            }
            // delete if empty
            collectionBox.getFunds().remove(currencyCode);
        }
        collectionBoxRepository.save(collectionBox);
        event.setFunds(event.getFunds() + moneyToTransfer);
        eventRepository.save(event);
        return new EmptyCollectionBoxRes(boxId, event.getId(), event.getFunds(), targetCurrency);
    }

    /**
     * fetches a collection box from the database, with checking if passed id is null
     */
    private CollectionBox fetchCollectionBox(Integer boxId) {
        if(boxId == null){
            throw new IllegalArgumentException("Box id cannot be empty");
        }
        Optional<CollectionBox> collectionBoxOptional = collectionBoxRepository.findById(boxId);
        if(collectionBoxOptional.isEmpty()){
            throw new EntityNotFoundException("Couldn't find collection box with id = " + boxId + " in the database");
        }
        return collectionBoxOptional.get();
    }

    /**
     * fetches an event from the database, with checking if passed id is null
     */
    private Event fetchEvent(Integer eventId) {
        if(eventId == null){
            throw new IllegalArgumentException("Event id cannot be empty");
        }
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            throw new EntityNotFoundException("Couldn't find event with id = " + eventId + " in the database");
        }
        return eventOptional.get();
    }
}
