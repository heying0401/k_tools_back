package com.kassen.hardlink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class hardlink {

    public String runPythonScript(String scriptPath, String arg1, String arg2) {
        ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptPath, arg1, arg2);
        processBuilder.redirectErrorStream(true); // This merges the error stream with the standard output stream

        StringBuilder output = new StringBuilder();
        Process process = null;
        try {
            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Handle the case where the script execution returns a non-zero exit code
                throw new RuntimeException("Python script exited with error code : " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            // Handle exceptions
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return output.toString();
    }


}
