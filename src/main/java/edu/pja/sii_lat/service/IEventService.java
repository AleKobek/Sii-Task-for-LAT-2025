package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.CreateEventReq;
import edu.pja.sii_lat.DTO.CreateEventRes;
import edu.pja.sii_lat.DTO.FinancialReportRes;

import java.util.List;

public interface IEventService {
    CreateEventRes createEvent(CreateEventReq req);

    List<FinancialReportRes> generateFinancialReport();
}
