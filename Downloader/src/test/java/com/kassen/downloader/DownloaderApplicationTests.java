package com.kassen.downloader;

import com.kassen.downloader.Service.DownloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class DownloaderApplicationTests {

    @Autowired
    DownloadService ds = new DownloadService();


    @Test
    void download() throws IOException {

        ds.downloadFile("https://img.freepik.com/free-vector/fresh-pumpkin-white-b_1308-39708.jpg?w=2000", "C:\\Users\\h-yu\\Downloads");
//        List<String> list = ds.scrapeImageLinks("https://www.shutterstock.com/search/pumpkin");

    }

}
