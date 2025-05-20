package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.BasicCollectionBoxDTO;
import edu.pja.sii_lat.DTO.CollectionBoxEventIdDTO;
import edu.pja.sii_lat.DTO.ListCollectionBoxResponse;
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


    // 2. Register a new collection box.
    public CollectionBoxEventIdDTO registerCollectionBox(Integer eventId){
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            throw new EntityNotFoundException("Couldn't find event with id = " + eventId + " in the database");
        }
        Event event = eventOptional.get();
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setEvent(event);
        event.getCollectionBoxSet().add(collectionBox);
        CollectionBox savedCollectionBox = collectionBoxRepository.save(collectionBox);
        eventRepository.save(event);
        return new CollectionBoxEventIdDTO(savedCollectionBox.getId(), eventId);
    }

    // 3. List all collection boxes. Include information if the box is assigned (but don’t expose to what
    //    fundraising event) and if it is empty or not (but don’t expose the actual value in the box).
    public List<ListCollectionBoxResponse> listCollectionBoxes(){
        List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll().stream().toList();
        List<ListCollectionBoxResponse> list = new ArrayList<>();
        for(CollectionBox collectionBox: collectionBoxes){
            list.add(new ListCollectionBoxResponse(
                    collectionBox.getId(), collectionBox.getEvent() == null,collectionBox.isEmpty()
            ));
        }
        return list;
    }

    // 4. Unregister (remove) a collection box (e.g. in case it was damaged or stolen).
    public void unregisterCollectionBox(Integer id){
        Optional<CollectionBox> collectionBoxOptional = collectionBoxRepository.findById(id);
        if(collectionBoxOptional.isEmpty()){
            throw new EntityNotFoundException("Couldn't find collection box with id = " + id + " in the database");
        }
        CollectionBox collectionBox = collectionBoxOptional.get();
        if(collectionBox.getEvent() == null){
            throw new RuntimeException("Box with id = " + id + "is not registered to an event");
        }
        collectionBox.getEvent().getCollectionBoxSet().remove(collectionBox);
        collectionBox.getFunds().clear();
        collectionBox.setEvent(null);
        collectionBoxRepository.save(collectionBox);
        collectionBoxRepository.delete(collectionBox);
    }

    // 5. Assign the collection box to an existing fundraising event.
    public BasicCollectionBoxDTO assignCollectionBox(CollectionBoxEventIdDTO dto){
        Integer boxId = dto.getIdBox();
        Integer eventId = dto.getIdEvent();

        if(boxId == null){
            throw new IllegalArgumentException("Box id cannot be empty");
        }
        if(eventId == null){
            throw new IllegalArgumentException("Event id cannot be empty");
        }

        Optional<CollectionBox> collectionBoxOptional = collectionBoxRepository.findById(boxId);
        if(collectionBoxOptional.isEmpty()){
            throw new EntityNotFoundException("Couldn't find collection box with id = " + boxId + " in the database");
        }
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            throw new EntityNotFoundException("Couldn't find event with id = " + eventId + " in the database");
        }

        CollectionBox collectionBox = collectionBoxOptional.get();
        Event event = eventOptional.get();

        if(collectionBox.getEvent() != null){
            throw new RuntimeException("This box is already assigned!");
        }
        if(!collectionBox.isEmpty()){
            throw new RuntimeException("This box is not empty!");
        }
        collectionBox.setEvent(event);
        event.getCollectionBoxSet().add(collectionBox);
        collectionBoxRepository.save(collectionBox);
        eventRepository.save(event);
        return new BasicCollectionBoxDTO(collectionBox.getId(), collectionBox.getEvent().getId(), collectionBox.getFunds());
    }





    /**
     * collects funds from all collection boxes, emptying them and retaining currencies
     * @return map with currency code as a key and funds as a value
     */
    private Map<String, Double> collectFunds(Event event){
        Map<String, Double> funds = new HashMap<>();
        // checking all collection boxes
        for(CollectionBox collectionBox: event.getCollectionBoxSet()){
            // map stores all funds in that collection box
            Map<String, Double> collectionBoxFunds = collectionBox.getFunds();
            // for every currency in that box
            for(String currency : collectionBoxFunds.keySet()){
                // update existing currency amount
                if(funds.containsKey(currency)){
                    double currentAmount = funds.get(currency);
                    currentAmount+=collectionBoxFunds.get(currency);
                    funds.replace(currency, currentAmount);
                    // add new currency
                }else {
                    funds.put(currency, collectionBoxFunds.get(currency));
                }
            }
            // emptying the box
            collectionBox.getFunds().clear();
        }
        return funds;
    }

}
