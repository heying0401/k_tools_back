package org.kassen.hardlink.Service;

import com.opencsv.CSVReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.kassen.hardlink.POJO.HardlinkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvHardlinkService {
    private static final Logger logger = LoggerFactory.getLogger(CsvHardlinkService.class);

    public HardlinkResponse processCsv(MultipartFile file, String baseDir) throws Exception {
        HardlinkResponse response = new HardlinkResponse();

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {
            String[] nextLine;

            // Skip the first line (header)
            csvReader.readNext();

            while ((nextLine = csvReader.readNext()) != null) {
                String vfxShotNo = nextLine[0];
                String type = nextLine[1];
                String reelPath = nextLine[2];

                String destinationPath = buildDestinationPath(vfxShotNo, type, baseDir);
                boolean success = createHardlink(reelPath, destinationPath);
                if (success) {
                    response.incrementSuccessCount();
                } else {
                    response.incrementFailureCount();
                    response.addFailedVFXShot(vfxShotNo);
                }
            }
            return response;
        }
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

        return baseDir + episodeNumber + "/" + vfxShotNo + "/" + vfxShotNo + "_" + type;
    }



    private boolean createHardlink(String source, String destination) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            // Ensure the destination directory exists
            String destinationDir = destination.substring(0, destination.lastIndexOf('/'));
            Files.createDirectories(Paths.get(destinationDir));

            processBuilder.command("bash", "-c", "cp -lruv \"" + source + "\" \"" + destination + "\"");
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
            logger.debug("Command executed, exit code: {}", exitCode);

            if (exitCode != 0) {
                logger.error("Hardlink creation command failed with exit code {}", exitCode);
                logger.error("Command output: {}", output.toString());
                return false;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to create hardlink from {} to {}", source, destination, e);
            return false;
        }
    }
}


