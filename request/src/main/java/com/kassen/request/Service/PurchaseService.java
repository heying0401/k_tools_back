package com.kassen.request.Service;

import com.kassen.request.POJO.PurchaseRequest;
import com.kassen.request.POJO.PurchaseRequestDTO;

import java.util.List;

public interface PurchaseService {

    boolean addPurchase (PurchaseRequestDTO purchaseRequestDTO);
    List<PurchaseRequest> loadRequests();
    boolean editPurchase (PurchaseRequestDTO purchaseRequestDTO);
    PurchaseRequest getPurchase(Integer id);

}
