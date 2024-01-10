package com.kassen.purchase.Service.Impl;

import com.kassen.purchase.Mapper.PurchaseMapper;
import com.kassen.purchase.POJO.PurchaseRequest;
import com.kassen.purchase.POJO.PurchaseRequestDTO;
import com.kassen.purchase.Service.PurchaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Override
    public boolean addPurchase(PurchaseRequestDTO purchaseRequestDTO) {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        BeanUtils.copyProperties(purchaseRequestDTO, purchaseRequest);
        int rowsAffected = purchaseMapper.insert(purchaseRequest);
        return rowsAffected > 0;
    }

    @Override
    public List<PurchaseRequest> loadRequests() {
        return purchaseMapper.loadRequests();
    }

    @Override
    public boolean editPurchase(PurchaseRequestDTO purchaseRequestDTO) {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        BeanUtils.copyProperties(purchaseRequestDTO, purchaseRequest);
        int rowsAffected = purchaseMapper.updateById(purchaseRequest);
        return rowsAffected > 0;
    }

    @Override
    public PurchaseRequest getPurchase(Integer id) {
        return purchaseMapper.selectById(id);
    }
}
