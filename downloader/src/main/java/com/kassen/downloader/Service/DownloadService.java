package com.kassen.downloader.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

@Service
public class DownloadService {

    public Map<String, Integer> downloadFile(Map<String, String> urlMap, String destination, boolean overwrite, boolean notCheckDuplicate){

        Map<String, Integer> response = new HashMap<>();
        response.put("downloaded", 0);
        response.put("replaced", 0);
        response.put("skipped", 0);
        response.put("error", 0);

        if (overwrite){
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                String s = downloadSingleFileOverwrite(entry.getValue(), destination);// here, entry.getValue() is the thumbnail URL
                response.put(s, response.get(s) + 1);
            }
            return response;
        } else {
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                String s = downloadSingleFile(entry.getValue(), destination, notCheckDuplicate);// here, entry.getValue() is the thumbnail URL
                response.put(s, response.get(s) + 1);
            }
            return response;
        }
    }

    public String downloadSingleFileOverwrite(String urlString, String destination) {

        String fileName;
        Path destinationDir;
        URL url;

        try {
            url = URI.create(urlString).toURL();
            fileName = extractImageNameFromUrl(url);
            destinationDir = Paths.get(destination);
            Path destinationFile = destinationDir.resolve(fileName);

            // Ensure the destination directory exists
            if (Files.notExists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            // Check if the file already exists to determine if it's an overwrite or a new download
            boolean isOverwrite = Files.exists(destinationFile);

            try (InputStream in = url.openStream()) {
                Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return isOverwrite ? "replaced" : "downloaded";
        } catch (IOException e) {
            return "error";
        }
    }

    public String downloadSingleFile(String urlString, String destination, boolean notCheckDuplicate) {

        String fileName;
        Path destinationDir;
        URL url;

        try {
            url = URI.create(urlString).toURL();
            fileName = extractImageNameFromUrl(url);
            destinationDir = Paths.get(destination);
            Path destinationFile = destinationDir.resolve(fileName);

            // Ensure the destination directory exists
            if (Files.notExists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            try (InputStream in = url.openStream()) {
                Files.copy(in, destinationFile);
                return "downloaded";
            } catch (FileAlreadyExistsException fae) {
                if (notCheckDuplicate){
                    String newFileName = generateUniqueFileName(fileName);
                    Path newDestinationFile = destinationDir.resolve(newFileName);
                    try (InputStream inAgain = url.openStream()) {
                        Files.copy(inAgain, newDestinationFile);
                        return "downloaded";
                    }
                } else {
                    return "skipped";
                }
            }
        } catch (IOException e) {
            return "error";
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
        return imageDetails;
    }

    private String extractImageNameFromUrl(URL url) {

        String path = url.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private String generateUniqueFileName(String originalFileName) {
        String baseName = fileNameWithoutExtension(originalFileName);
        String extension = fileExtension(originalFileName);
        int counter = 1;

        String newFileName = baseName + "_copy" + counter + extension;
        while (Files.exists(Paths.get(newFileName))) {
            counter++;
            newFileName = baseName + "_copy" + counter + extension;
        }

        return newFileName;
    }

    public static String fileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        } else {
            return fileName;
        }
    }

    public static String fileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        } else {
            return "";
        }
    }
}
