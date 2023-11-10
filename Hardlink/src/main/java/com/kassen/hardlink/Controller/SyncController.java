package com.kassen.hardlink.Controller;

import com.kassen.hardlink.POJO.SyncOperation;
import com.kassen.hardlink.Service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping
public class SyncController {

    private static final Logger log = LoggerFactory.getLogger(SyncController.class);

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/addSync")
    public ResponseEntity<?> addSync(@RequestBody SyncOperation syncOperation) {

        log.info("Received sync operation: {}", syncOperation);
        SyncOperation createdSyncOperation = syncService.addSync(syncOperation);
        if (createdSyncOperation != null) {
            // Operation was successful
            return ResponseEntity.ok(createdSyncOperation);
        } else {
            // Operation failed
            return ResponseEntity.badRequest().body("Sync operation could not be added");
        }
    }

    @GetMapping("/load")
    public ResponseEntity<List<SyncOperation>> getSyncList() {
        List<SyncOperation> operations = syncService.getSyncList();
        return ResponseEntity.ok(operations);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteSync(@PathVariable Integer id) {
        int result = syncService.deleteSync(id);
        if (result == 1) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
