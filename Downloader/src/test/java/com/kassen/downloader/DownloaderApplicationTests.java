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


}
