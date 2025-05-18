package edu.pja.sii_lat.service;

import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import edu.pja.sii_lat.repository.CollectionBoxRepository;
import edu.pja.sii_lat.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventService {

    EventRepository eventRepository;
    CollectionBoxRepository collectionBoxRepository;


    /**
     * gets funds from all collection boxes, retaining their currencies
     * @return map with currency code as a key and funds as a value
     */
    public Map<String, Double> getFunds(Event event){
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
        }
        return funds;
    }

    /**
     * sets event of that box, checking if it is not empty
     * @param box
     * @param event
     */
    public void setBoxEvent(CollectionBox box, Event event) {
        for(String currency : box.getFunds().keySet()){
            if(box.getFunds().get(currency) > 0) {
                throw new IllegalArgumentException("This box is not empty!");
            }
        }
        box.setEvent(event);
    }
}
