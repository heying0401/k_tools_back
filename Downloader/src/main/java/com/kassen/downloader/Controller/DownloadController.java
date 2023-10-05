package com.kassen.downloader.Controller;

import com.kassen.downloader.POJO.DownloadRequest;
import com.kassen.downloader.Service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*")
public class DownloadController {

    @Autowired
    DownloadService downloadService = new DownloadService();

    @PostMapping("/download")
    public ResponseEntity<Map<String, Integer>> download(@RequestBody DownloadRequest dq) throws IOException {
        String url = dq.getUrl();
        String destination = dq.getDestination();
        System.out.println(destination);
        List<String> filesExisted = dq.getFilesExisted();
        Boolean overwrite = dq.getOverwrite();

        Map<String, String> imageLinks = downloadService.scrapeImageLinks(url);
        Map<String, Integer> response = new HashMap<>();

        if (filesExisted.isEmpty()){
            int downloadedFiles = downloadService.downloadFile(imageLinks, destination);
            response.put("downloaded", downloadedFiles);
            response.put("replaced", 0);
            response.put("skipped", 0);
            response.put("error", 0);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        List<String> fileNumberExisted = new ArrayList<>();

        for (String s : filesExisted) {
            String s1 = downloadService.extractNumber(s);
            fileNumberExisted.add(s1);
        }

        int error = 0;
        int downloaded = 0;
        int replaced = 0;
        int skipped = 0;

        for (Map.Entry<String, String> entry : imageLinks.entrySet()) {
            String urlNumber = downloadService.extractNumber(entry.getKey());
            boolean fileExists = fileNumberExisted.contains(urlNumber);

            if (fileExists && !overwrite) {
                skipped++;
                continue;
            }

            boolean downloadStatus = downloadService.downloadSingleFile(entry.getValue(), destination);

            if (downloadStatus) {
                downloaded++;
                if (fileExists) replaced++;
            } else {
                error++;
            }
        }

        response.put("downloaded", downloaded);
        response.put("replaced", replaced);
        response.put("skipped", skipped);
        response.put("error", error);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}