package com.kassen.downloader.Controller;

import com.kassen.downloader.Service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*")
public class DownloadController {

    @Autowired
    DownloadService downloadService = new DownloadService();

    @PostMapping("/download")
    public boolean download(@RequestBody Map<String, String> payload) throws IOException {
        String url = payload.get("url");
        String destination = payload.get("destination");

//        System.out.println(url);
//        System.out.println(destination);

        Map<String, String> imageLinks = downloadService.scrapeImageLinks(url);
        return downloadService.downloadFile(imageLinks, destination);
    }
}
