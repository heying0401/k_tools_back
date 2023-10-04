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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DownloadService {

    public int downloadFile(Map<String, String> urlMap, String destination) throws IOException {

        int number = 0;

        try {
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                downloadSingleFile(entry.getValue(), destination);// here, entry.getValue() is the thumbnail URL
                number++;
            }
            return number;
        } catch (IOException e) {
            return 0;
        }
    }

    public boolean downloadSingleFile(String urlString, String destination) throws IOException {

        try {
            URL url = URI.create(urlString).toURL();
            Path destinationDir = Paths.get(destination);

            String fileName = extractImageNameFromUrl(url);
            Path destinationFile = destinationDir.resolve(fileName);

            // Ensure the destination directory exists
            if (Files.notExists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            try (InputStream in = url.openStream()) {
                Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Map<String, String> scrapeImageLinks(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        // Selecting the anchor tags within the relevant div containers
        Elements anchorElements = doc.select("div[data-automation=AssetGrids_GridItemContainer_div] a[data-automation=AssetGrids_GridItemClickableArea_link]");

        Map<String, String> imageDetails = new HashMap<>();

        for (Element anchor : anchorElements) {
            // Extract details page URL
            String detailsPageUrl = anchor.absUrl("href");

            // Extract thumbnail URL from the associated image tag
            String thumbnailUrl = anchor.parent().selectFirst("img.mui-1l7n00y-thumbnail").absUrl("src");

            imageDetails.put(detailsPageUrl, thumbnailUrl);
        }
//        System.out.println(imageDetails.keySet());
//        System.out.println(imageDetails.values());

        return imageDetails;
    }

    public String extractNumber(String input) {
        Pattern pattern = Pattern.compile("[-_](\\d+)(\\..*|$)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null; // or throw an exception or return a default value
    }



    private String extractImageNameFromUrl(URL url) {

        String path = url.getPath();
        return path.substring(path.lastIndexOf('/') + 1);

//        String[] parts = url.getPath().split("/");
//        String lastPart = parts[parts.length - 1];
//        // Split by '-' and take everything but the first segment (assuming the structure remains consistent)
//        String[] nameParts = lastPart.split("-");
//        return String.join("-", Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
    }
}
