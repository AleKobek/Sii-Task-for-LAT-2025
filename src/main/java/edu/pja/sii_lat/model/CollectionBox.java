package edu.pja.sii_lat.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "collection_box_funds", joinColumns = @JoinColumn(name = "collection_box_id"))
    @MapKeyColumn(name = "currency")
    private Map<String, Double> funds = new HashMap<>();




    /**
     * adds funds to the box, retaining its currency
     * @param currencyCode code of the donation's currency
     * @param amount amount of donated money
     */
    public void addFunds(String currencyCode, double amount){
        if(funds.containsKey(currencyCode)){
            funds.replace(currencyCode, funds.get(currencyCode)+amount);
        }else {
            funds.put(currencyCode,amount);
        }
    }


    /**
     * checks if the box is empty
     * @return true if the box is empty, false if it's not
     */
    public boolean isEmpty(){
        for(String currency : funds.keySet()){
            if(funds.get(currency) > 0) {
                return false;
            }
        }
        return true;
    }
}
