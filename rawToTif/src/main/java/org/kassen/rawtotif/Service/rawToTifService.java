package org.kassen.rawtotif.Service;

import org.kassen.rawtotif.Controller.rawToTifController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class rawToTifService {

    private static final Logger logger = LoggerFactory.getLogger(rawToTifService.class);


    @Async
    public void processDirectoryAsync(String rawDir, SseEmitter emitter) {
        try {
            logger.info("Started processing directory: {}", rawDir);
            processDirectory(rawDir, emitter);
            emitter.complete();
            logger.info("Completed sending events for: {}", rawDir);
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Error: " + e.getMessage()));
            } catch (IOException ex) {
                logger.error("Failed to send error message", ex);
                emitter.completeWithError(ex);
            }
        }
    }

    public void processDirectory(String currentDir, SseEmitter emitter) {
        File dir = new File(currentDir);
        if (!dir.exists() || !dir.isDirectory()) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Invalid directory: " + currentDir));
            } catch (IOException e) {
                emitter.completeWithError(e);
                return;
            }
            emitter.complete();
            return;
        }

        try {
            emitter.send(SseEmitter.event().name("status").data("Processing directory: " + currentDir));
            logger.info("Sent SSE message: Processing directory {}", currentDir);
        } catch (IOException e) {
            emitter.completeWithError(e);
            return;
        }

        String outputDir = "";
        boolean outputDirSet = false;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !"tiff".equals(file.getName())) {
                    processDirectory(file.getAbsolutePath(), emitter);
                } else if (file.getName().matches(".*\\.(CR2|CR3|ARW)$")) {
                    if (!outputDirSet) {
                        outputDir = new File(file.getParentFile().getParent(), "tiff").getAbsolutePath();
                        new File(outputDir).mkdirs();
                        try {
                            emitter.send(SseEmitter.event().name("output").data("Output will be placed in: " + outputDir));
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                            return;
                        }
                        outputDirSet = true;
                    }
                    processRawFile(file, outputDir, emitter);
                }
            }
        }
        emitter.complete();
    }

    public void processRawFile(File file, String outputDir, SseEmitter emitter) {
        ProcessBuilder builder = new ProcessBuilder(
                "dcraw_emu", "-v", "+M", "-w", "-W", "-H", "0", "-o", "0", "-q", "11", "-g", "2.4", "12.92", "-6", "-T",
                "-Z", outputDir + "/" + file.getName().replaceFirst("[.][^.]+$", "") + ".tif",
                file.getPath()
        );

        try {
            Process process = builder.start();


            // Reading output from the command
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info(line); // Log output for debugging
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                emitter.send(SseEmitter.event().name("processed").data("Processed successfully: " + file.getPath()));
            } else {
                emitter.send(SseEmitter.event().name("error").data("Error processing file: " + file.getPath() + ". See server logs for more details."));
            }
        } catch (IOException | InterruptedException e) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Error executing dcraw_emu: " + e.getMessage()));
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }
}
