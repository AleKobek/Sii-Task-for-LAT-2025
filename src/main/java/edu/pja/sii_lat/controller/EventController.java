package edu.pja.sii_lat.controller;


import edu.pja.sii_lat.DTO.CreateEventReq;
import edu.pja.sii_lat.DTO.CreateEventRes;
import edu.pja.sii_lat.DTO.FinancialReportRes;
import edu.pja.sii_lat.service.IEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;

    /**
     * 1. Create a new fundraising event.
     * @param req name and currency code of the event
     * @return id, name and currency code of the created event
     */
    @PostMapping
    public ResponseEntity<CreateEventRes> createEvent(@RequestBody CreateEventReq req){
        CreateEventRes res = eventService.createEvent(req);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * 8. Display a financial report with all fundraising events and the sum of their accounts.
     * @return list of events with their names, currencies and collected funds
     */
    @GetMapping("/financialReport")
    public ResponseEntity<List<FinancialReportRes>> financialReport(){
        List<FinancialReportRes> res = eventService.generateFinancialReport();
        return ResponseEntity.ok(res);
    }
}
