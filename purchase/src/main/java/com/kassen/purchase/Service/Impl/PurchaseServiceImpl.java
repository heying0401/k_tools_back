package com.kassen.purchase.Service.Impl;

import com.kassen.purchase.Mapper.PurchaseMapper;
import com.kassen.purchase.POJO.PurchaseRequest;
import com.kassen.purchase.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    PurchaseMapper purchaseMapper;

    @Override
    public int addPurchase(PurchaseRequest purchaseRequest) {
        return purchaseMapper.insert(purchaseRequest);
    }
}
