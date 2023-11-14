package com.kassen.hardlink.Controller;

import com.kassen.hardlink.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sync/logs")
@CrossOrigin(origins = "*")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getLatestLogs(@RequestParam(defaultValue = "100") int lineCount) {
        try {
            List<String> logs = logService.readLastNLinesFromLogFile(lineCount);
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

