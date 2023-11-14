package com.kassen.hardlink.Service;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LogService {

    private final Path logFilePath = Paths.get("/home/h-yu/Documents/synclogs.log");

    public List<String> readLastNLinesFromLogFile(int lineCount) throws IOException {
//        System.out.println(logFilePath);
        List<String> result = new ArrayList<>();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(logFilePath.toFile(), Charset.defaultCharset())) {
            String line;
            while ((line = reader.readLine()) != null && result.size() < lineCount) {
//                System.out.println("LINE is" + line);
                result.add(line);
            }
        }
        Collections.reverse(result); // Reverse to maintain the order of the logs
        return result;
    }
}

