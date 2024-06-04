package org.kassen.rawtotif.Controller;

import org.kassen.rawtotif.Service.rawToTifService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class rawToTifController {

    private static final Logger logger = LoggerFactory.getLogger(rawToTifController.class);

    @Autowired
    private rawToTifService rawToTifService;

    @PostMapping("/raw")
    public String uploadFileAndCreateHardlinks(
            @RequestParam("rawDir") String rawDir) {
        try {
            rawToTifService.processDirectory(rawDir);
//            HardlinkResponse response = csvHardlinkService.processCsv(rawDir);
            return "OK";
        } catch (Exception e) {
            logger.error("Error processing file", e);
            return "not ok";
        }
    }
}
