package com.kassen.hardlink.Controller;

import com.kassen.hardlink.POJO.SyncOperation;
import com.kassen.hardlink.Service.SyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createSyncOperation(@RequestBody SyncOperation syncOperation) {
        Integer i = syncService.addSync(syncOperation);
        if (i == 1) {
            // Operation was successful
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // Operation failed
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
