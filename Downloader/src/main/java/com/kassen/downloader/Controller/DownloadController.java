package com.kassen.downloader.Controller;

import com.kassen.downloader.Service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;


@RestController
@CrossOrigin(origins = "*")
public class DownloadController {

    @Autowired
    DownloadService downloadService = new DownloadService();

    @GetMapping("/download")
    public ResponseEntity<String> download(@RequestParam String url, @RequestParam String localPath) throws IOException {
        List<String> imageLinks = downloadService.scrapeImageLinks(url);
//        for(String link : imageLinks) {
//            String fileName = extractFileNameFromLink(link); // Implement this
//            if(!downloadService.doesFileExist(localPath, fileName)) {
//                downloadService.downloadFile(link, Paths.get(localPath + fileName));
//            }
//        }
        return ResponseEntity.ok("Download completed");
    }
}
