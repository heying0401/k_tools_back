package com.kassen.request.Controller;

import com.kassen.request.POJO.PurchaseRequest;
import com.kassen.request.POJO.PurchaseRequestDTO;
import com.kassen.request.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);


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

    @GetMapping("/getPurchase/{id}")
    public PurchaseRequest getPurchase(@PathVariable Integer id){
        return purchaseService.getPurchase(id);
    }

    @PutMapping("/editPurchase")
    public ResponseEntity<?> editPurchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        logger.debug("Received purchase request: {}", purchaseRequestDTO);
        boolean isUpdated = purchaseService.editPurchase(purchaseRequestDTO);
        if (isUpdated) {
            return new ResponseEntity<>("Purchase request updated successfully.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to create purchase request", HttpStatus.BAD_REQUEST);
        }
    }
}
