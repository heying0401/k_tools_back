package com.kassen.request.Service.Impl;

import com.kassen.request.Mapper.PurchaseMapper;
import com.kassen.request.POJO.PurchaseRequest;
import com.kassen.request.POJO.PurchaseRequestDTO;
import com.kassen.request.Service.PurchaseService;
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
