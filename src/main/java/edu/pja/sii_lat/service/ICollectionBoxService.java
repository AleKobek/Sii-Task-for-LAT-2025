package edu.pja.sii_lat.service;

import edu.pja.sii_lat.DTO.*;

import java.util.List;

public interface ICollectionBoxService {
    CreateCollectionBoxRes registerCollectionBox();
    List<ListCollectionBoxResponse> listCollectionBoxes();
    void unregisterCollectionBox(Integer boxId);
    BasicCollectionBoxDTO assignCollectionBox(AssignCollectionBoxReq req);
    BasicCollectionBoxDTO depositMoney(DepositMoneyReq req);
    EmptyCollectionBoxRes emptyCollectionBox(Integer boxId);
}
