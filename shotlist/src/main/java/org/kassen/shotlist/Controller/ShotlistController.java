package org.kassen.shotlist.Controller;

import org.kassen.shotlist.Service.ShotlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class ShotlistController {

    public final ShotlistService shotlistService;

    @Autowired
    public ShotlistController(ShotlistService shotlistService) {
        this.shotlistService = shotlistService;
    }

    @PostMapping("/shotlist")
    public ResponseEntity<String> processDirectory(@RequestBody String directory) {
        System.out.println(directory);
        try {
            String outputpath = shotlistService.processDirectory(directory.trim());
            return ResponseEntity.ok(outputpath);
        } catch (Exception e) {
            // Log error details here
            return ResponseEntity.internalServerError().body("Failed to process directory: " + e.getMessage());
        }
    }
}
