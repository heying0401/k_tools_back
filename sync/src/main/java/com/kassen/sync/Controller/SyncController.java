package com.kassen.sync.Controller;

import com.kassen.sync.POJO.SyncOperation;
import com.kassen.sync.Service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @Transactional
    public ResponseEntity<?> addSync(@RequestBody SyncOperation syncOperation) {

        log.info("Received sync operation: {}", syncOperation);

        Integer result = syncService.addSync(syncOperation);
        if (result == 1) {
            // Operation was successful
            return ResponseEntity.ok().build();
        } else {
            // Operation failed
            return ResponseEntity.badRequest().body("Sync operation could not be added");
        }
    }

    @GetMapping("/load")
    public ResponseEntity<List<SyncOperation>> getSyncList() {
        List<SyncOperation> operations = syncService.getAll();
        return ResponseEntity.ok(operations);
    }

    @PutMapping("/updateStatus/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> updateSyncStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            SyncOperation.SyncStatus newStatus = SyncOperation.SyncStatus.valueOf(status);
            int result = syncService.updateOneStatus(id, newStatus);
            if (result == 1) {
                return ResponseEntity.ok().build(); // Successfully updated the status
            } else {
                return ResponseEntity.notFound().build(); // The sync operation was not found
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // The provided status was invalid
        }
    }
}
