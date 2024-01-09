package com.kassen.purchase.Service;

import com.kassen.purchase.POJO.PurchaseRequest;
import com.kassen.purchase.POJO.PurchaseRequestDTO;

import java.util.List;

public interface PurchaseService {

    boolean addPurchase (PurchaseRequestDTO purchaseRequestDTO);
    List<PurchaseRequest> loadRequests();
    boolean editPurchase (PurchaseRequestDTO purchaseRequestDTO);
    PurchaseRequest getPurchase(Integer id);

}
