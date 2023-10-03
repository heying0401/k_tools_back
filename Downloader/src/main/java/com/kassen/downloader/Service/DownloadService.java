package com.kassen.downloader.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DownloadService {

    public void downloadFile(String urlString, String destination) throws IOException{

        URL url = URI.create(urlString).toURL();
        Path destinationDir = Paths.get(destination);

        String fileName = extractImageNameFromUrl(url) + ".jpg";
        Path destinationFile = destinationDir.resolve(fileName);

        // Ensure the destination directory exists
        if (Files.notExists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        try (InputStream in = url.openStream()) {
            Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public List<String> scrapeImageLinks(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        Elements imageElements = doc.select("img.your_selector_here");
        List<String> imageLinks = new ArrayList<>();
        for (Element imgElement : imageElements) {
            String src = imgElement.absUrl("src");
            imageLinks.add(src);
        }

        return imageLinks;
    }

    public boolean doesFileExist(String path, String filename) {
        File file = new File(path + filename);
        return file.exists();
    }

    private String extractImageNameFromUrl(URL url) {
        String[] parts = url.getPath().split("/");
        String lastPart = parts[parts.length - 1];
        // Split by '-' and take everything but the first segment (assuming the structure remains consistent)
        String[] nameParts = lastPart.split("-");
        return String.join("-", Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
    }

}
