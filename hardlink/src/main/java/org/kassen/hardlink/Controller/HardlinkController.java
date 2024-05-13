package org.kassen.hardlink.Controller;

import org.kassen.hardlink.POJO.HardlinkResponse;
import org.kassen.hardlink.Service.CsvHardlinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@CrossOrigin(origins = "*")
public class HardlinkController {

    private static final Logger logger = LoggerFactory.getLogger(HardlinkController.class);


    @Autowired
    private CsvHardlinkService csvHardlinkService;

    @PostMapping("/hardlink")
    public ResponseEntity<HardlinkResponse> uploadFileAndCreateHardlinks(
            @RequestParam("file") MultipartFile file,
            @RequestParam("baseDir") String baseDir) {
//        logger.info("base dir: {}", baseDir);
        try {
            HardlinkResponse response = csvHardlinkService.processCsv(file, baseDir);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
