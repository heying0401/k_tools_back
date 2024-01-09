package com.kassen.purchase.Controller;

import com.kassen.purchase.POJO.PurchaseRequest;
import com.kassen.purchase.POJO.PurchaseRequestDTO;
import com.kassen.purchase.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/addPurchase")
    public ResponseEntity<?> addPurchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        System.out.println(purchaseRequestDTO);
        boolean isAdded = purchaseService.addPurchase(purchaseRequestDTO);
        if (isAdded) {
            return new ResponseEntity<>("Purchase request added successfully.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to create purchase request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/loadRequests")
    public List<PurchaseRequest> loadRequests(){
        return purchaseService.loadRequests();
    }

    @GetMapping("/getPurchase")
    public PurchaseRequest getPurchase(Integer id){
        return purchaseService.getPurchase(id);
    }

    @PostMapping("/editPurchase")
    public ResponseEntity<?> editPurchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        System.out.println(purchaseRequestDTO);
        boolean isAdded = purchaseService.addPurchase(purchaseRequestDTO);
        if (isAdded) {
            return new ResponseEntity<>("Purchase request added successfully.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to create purchase request", HttpStatus.BAD_REQUEST);
        }
    }
}
