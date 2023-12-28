package com.kassen.purchase.Service.Impl;

import com.kassen.purchase.Mapper.PurchaseMapper;
import com.kassen.purchase.POJO.PurchaseRequest;
import com.kassen.purchase.POJO.PurchaseRequestDTO;
import com.kassen.purchase.Service.PurchaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Override
    public boolean addPurchase(PurchaseRequestDTO purchaseRequestDTO) {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        BeanUtils.copyProperties(purchaseRequestDTO, purchaseRequest);

        LocalDateTime now = LocalDateTime.now();
        purchaseRequest.setCreatedAt(now);
        purchaseRequest.setUpdatedAt(now);

        int rowsAffected = purchaseMapper.insert(purchaseRequest);
        return rowsAffected > 0;
    }
}
