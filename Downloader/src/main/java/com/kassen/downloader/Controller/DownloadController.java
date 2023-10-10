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
//        System.out.println(destination);
        List<String> filesExisted = dq.getFilesExisted();
//        System.out.println(filesExisted);
        boolean notCheckDuplicate = filesExisted.isEmpty();
        boolean overwrite = dq.getOverwrite();

//        System.out.println("is overwrite: " + overwrite);
//        System.out.println("is notCheckDuplicate: " + notCheckDuplicate);

        Map<String, String> imageLinks = downloadService.scrapeImageLinks(url);
        Map<String, Integer> response = downloadService.downloadFile(imageLinks, destination, overwrite, notCheckDuplicate);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}