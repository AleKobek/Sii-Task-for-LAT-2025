package edu.pja.sii_lat.controller;


import edu.pja.sii_lat.DTO.*;
import edu.pja.sii_lat.service.ICollectionBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectionBoxes")
@RequiredArgsConstructor
public class CollectionBoxController {

    private final ICollectionBoxService collectionBoxService;

    /**
     * 2. Register a new collection box.
     * @return id and funds of the created box
     */
    @PostMapping
    public ResponseEntity<CreateCollectionBoxRes> registerCollectionBox(){
        CreateCollectionBoxRes res = collectionBoxService.registerCollectionBox();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * 3. List all collection boxes. Include information if the box is assigned (but don’t expose to what
     * fundraising event) and if it is empty or not (but don’t expose the actual value in the box).
     * @return list of (event id, is box assigned, is box empty) for every box
     */
    @GetMapping
    public ResponseEntity<List<ListCollectionBoxResponse>> listCollectionBoxes(){
         List<ListCollectionBoxResponse> res = collectionBoxService.listCollectionBoxes();
         return ResponseEntity.ok(res);
    }

    /**
     * 4. Unregister (remove) a collection box (e.g. in case it was damaged or stolen).
     * @param boxId id of the box to unregister
     */
    @DeleteMapping("/{boxId}")
    public ResponseEntity<Void> unregisterCollectionBox(@PathVariable Integer boxId){
        collectionBoxService.unregisterCollectionBox(boxId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 5. Assign the collection box to an existing fundraising event.
     * @param req id of the box and id of the event
     * @return id of the assigned box, id of the assigned event, and map of the box's funds
     */
    @PutMapping("/assign")
    public ResponseEntity<BasicCollectionBoxDTO> assignCollectionBox(@RequestBody AssignCollectionBoxReq req){
        BasicCollectionBoxDTO res = collectionBoxService.assignCollectionBox(req);
        return ResponseEntity.ok(res);
    }

    /**
     * 6. Put (add) some money inside the collection box.
     * @param req id of the box, amount of money to deposit and currency of the money
     * @return id of the box, id of the event assigned to it, and map of the box's funds - currency code and amount of money
     */
    @PutMapping("/deposit")
    public ResponseEntity<BasicCollectionBoxDTO> depositMoney(@RequestBody DepositMoneyReq req){
        BasicCollectionBoxDTO res = collectionBoxService.depositMoney(req);
        return ResponseEntity.ok(res);
    }

    /**
     * 7. Empty the collection box i.e. “transfer” money from the box to the fundraising event’s account.
     * @param boxId id of the box to empty
     * @return id of the emptied box, id of the event assigned to it and the box's funds
     */
    @PutMapping("/{boxId}")
    public ResponseEntity<EmptyCollectionBoxRes> emptyCollectionBox(@PathVariable Integer boxId){
        EmptyCollectionBoxRes res = collectionBoxService.emptyCollectionBox(boxId);
        return ResponseEntity.ok(res);
    }
}
