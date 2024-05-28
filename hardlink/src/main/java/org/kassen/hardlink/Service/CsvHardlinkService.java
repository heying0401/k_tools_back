package org.kassen.hardlink.Service;

import com.opencsv.CSVReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.kassen.hardlink.POJO.HardlinkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvHardlinkService {
    private static final Logger logger = LoggerFactory.getLogger(CsvHardlinkService.class);

    public HardlinkResponse processCsv(MultipartFile file, String baseDir) {
        HardlinkResponse response = new HardlinkResponse();

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {
            String[] nextLine;

            // Skip the first line (header)
            csvReader.readNext();

            while ((nextLine = csvReader.readNext()) != null) {
                response.incrementTotalCount();
                try {
                    String vfxShotNo = nextLine[0];
                    String type = nextLine[1];
                    String reelPath = nextLine[2];

                    String destinationPath = buildDestinationPath(vfxShotNo, type, baseDir);
                    logger.info("Destination path: {}", destinationPath);

                    if (createHardlink(reelPath, destinationPath, vfxShotNo, response)) {
                        response.incrementSuccessCount();
                    } else {
                        response.incrementFailureCount();
                        response.addFailedVFXShot(vfxShotNo);
                    }
                } catch (Exception e) {
                    logger.error("Error processing line: {}", (Object) nextLine, e);
                    response.incrementFailureCount();
                    if (nextLine != null && nextLine.length > 0) {
                        response.addFailedVFXShot(nextLine[0]);
                    }                }
            }
        } catch (Exception e) {
            logger.error("Failed to process CSV file", e);
        }
        return response;
    }


    private String buildDestinationPath(String vfxShotNo, String type, String baseDir) {
        // Extract episode number dynamically between first and second underscore
        int firstUnderscore = vfxShotNo.indexOf('_');
        int secondUnderscore = vfxShotNo.indexOf('_', firstUnderscore + 1);
        String episodeNumber = vfxShotNo.substring(firstUnderscore + 1, secondUnderscore);

        // Ensure the baseDir ends with a slash
        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }

        String fileName = baseDir.substring(baseDir.lastIndexOf('/') + 1);


        return baseDir + episodeNumber + "/" + vfxShotNo + "/" + vfxShotNo + "_" + type + "/" + fileName;
    }

    private boolean createHardlink(String source, String destination, String vfxShotNo, HardlinkResponse response) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            File sourceFile = new File(source);
            logger.info("Checking if source file exists: {}", sourceFile.exists());
            logger.info("Readable: {}, Writable: {}", sourceFile.canRead(), sourceFile.canWrite());

            String fileName = source.substring(source.lastIndexOf('/') + 1);
            String fullDestinationPath = destination + fileName;

            logger.info("fullDestinationPath is: {}", fullDestinationPath);

            File destFile = new File(fullDestinationPath);
            boolean fileExists = destFile.exists();


//            if (destFile.exists()) {
//                logger.info("Destination already exists: {}", destination);
//                return false;  // Or handle as needed if overwriting is intended
//            }

            // Ensure the destination directory exists
            String destinationDir = destination.substring(0, destination.lastIndexOf('/'));
            Files.createDirectories(Paths.get(destinationDir));

            if (!Files.exists(Paths.get(destinationDir))) {
                logger.error("Failed to create directory: {}", destinationDir);
                return false;
            }

            String command = "cp -lruv \"" + source + "\" \"" + destination + "\"";
            logger.info("Executing command: {}", command);

            processBuilder.command("bash", "-c", command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                logger.debug("cp command output: {}", line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Command output: {}", output.toString());
                return false;
            }
            if (fileExists) {
                response.addOverwrittenFile(vfxShotNo); // Record the overwritten file
            }
            return true;
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to create hardlink from {} to {}", source, destination, e);
            return false;
        }
    }
}


