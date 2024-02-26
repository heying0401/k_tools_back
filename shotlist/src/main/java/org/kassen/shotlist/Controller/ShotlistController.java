package org.kassen.shotlist.Controller;

import org.kassen.shotlist.Service.ShotlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class ShotlistController {
    private static final Logger logger = LoggerFactory.getLogger(ShotlistController.class);

    public final ShotlistService shotlistService;

    @Autowired
    public ShotlistController(ShotlistService shotlistService) {
        this.shotlistService = shotlistService;
    }

    @PostMapping("/shotlist")
    public ResponseEntity<String> processDirectory(@RequestBody String directory) {
        logger.info("Received request to process directory: {}", directory);
        try {
            String outputpath = shotlistService.processDirectory(directory.trim());
            logger.info("Directory processed successfully, output path: {}", outputpath);
            return ResponseEntity.ok(outputpath);
        } catch (Exception e) {
            logger.error("Failed to process directory: {}", directory, e);
            return ResponseEntity.internalServerError().body("Failed to process directory: " + e.getMessage());
        }
    }
}
