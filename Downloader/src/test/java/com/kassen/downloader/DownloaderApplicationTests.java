package com.kassen.downloader;

import com.kassen.downloader.Service.DownloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
class DownloaderApplicationTests {

    @Autowired
    DownloadService ds = new DownloadService();


    @Test
    void scrap() throws IOException {
        Map<String, String> map = ds.scrapeImageLinks("https://www.shutterstock.com/search/pumpkin");

        ds.downloadFile(map, "D:\\Misc\\testdownload");
        // Using Jackson
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonString = objectMapper.writeValueAsString(map);
//        System.out.println(jsonString);

    }

    @Test
    void numberString(){
        String s = ds.extractNumber("shutterstock_1934050523");
        System.out.println(s);
    }
}
