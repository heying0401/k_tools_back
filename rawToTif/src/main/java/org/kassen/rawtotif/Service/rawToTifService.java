package org.kassen.rawtotif.Service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class rawToTifService {

    public void processDirectory(String currentDir) {
        File dir = new File(currentDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid directory: " + currentDir);
            return;
        }

        System.out.println("Processing directory: " + currentDir);
        String outputDir = "";
        boolean outputDirSet = false;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!"tiff".equals(file.getName())) {
                        processDirectory(file.getAbsolutePath());
                    }
                } else if (file.getName().matches(".*\\.(CR2|CR3|ARW)$")) {
                    if (!outputDirSet) {
                        outputDir = new File(file.getParentFile().getParent(), "tiff").getAbsolutePath();
                        new File(outputDir).mkdirs();
                        System.out.println("Output will be placed in: " + outputDir);
                        outputDirSet = true;
                    }
                    processRawFile(file, outputDir);
                }
            }
        }
    }

    public void processRawFile(File file, String outputDir) {
        ProcessBuilder builder = new ProcessBuilder(
                "dcraw_emu", "-v", "+M", "-w", "-W", "-H", "0", "-o", "0", "-q", "11", "-g", "2.4", "12.92", "-6", "-T",
                "-Z", outputDir + "/" + file.getName().replaceFirst("[.][^.]+$", "") + ".tif",
                file.getPath()
        );

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Processed successfully: " + file.getPath());
            } else {
                System.out.println("Error processing file: " + file.getPath());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing dcraw_emu: " + e.getMessage());
        }
    }


}
