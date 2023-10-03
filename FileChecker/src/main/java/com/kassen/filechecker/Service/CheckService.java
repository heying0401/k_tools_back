package com.kassen.filechecker.Service;

import com.kassen.filechecker.POJO.CheckRequest;
import com.kassen.filechecker.POJO.CheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CheckService{

    public CheckResult checkFileNumber(Set<String> uniqueSub, Map<String, Integer> csvContent) {

//        System.out.println(uniqueSub);

        CheckResult cr = new CheckResult();
        int numCsvRows = csvContent.size();
        int numSubDirectories = uniqueSub.size();

        if (numCsvRows == numSubDirectories) {
            cr.setStatus(true);
            cr.setMsg("ファイルの数とCSVが一致しています。");
        } else {
            cr.setStatus(false);
            cr.setMsg("ファイルの数（" + numSubDirectories + "）とCSV（" + numCsvRows + "）が一致していません。");
        }
        return cr;
    }

    public CheckResult checkFolderNames(Set<String> uniqueSub, Map<String, Integer> csvContent) {

        CheckResult cr = new CheckResult();

        for (String csvFolder : csvContent.keySet()) {
            if (!uniqueSub.contains(csvFolder)) {
                cr.setStatus(false);
                cr.setMsg("CSVに指定されたファイル " + csvFolder + " がPCに見つかりませんでした。");
                return cr;
            }
        }

        cr.setStatus(true);
        cr.setMsg("ファイルの名称とCSVが一致しています。");
        return cr;
    }


    public CheckResult checkTextMatch(List<String> normalizedDirectories, Map<String, Integer> csvContent) {

        System.out.println("csv Content is: " + csvContent);

        CheckResult result = new CheckResult();
        Map<String, Long> folderFileCounts = new HashMap<>();

        // Count the number of files in each sub-directory.
        for (String directory : normalizedDirectories) {
            String[] parts = directory.split("/");
            if (parts.length >= 2) {
                String subDirectory = parts[0]; // Assuming 'directory/subdirectory/file'.
                folderFileCounts.put(subDirectory, folderFileCounts.getOrDefault(subDirectory, 0L) + 1);
            }
        }

        System.out.println("folderFileCounts is: " + folderFileCounts);

        // Check if the counts match with the CSV content.
        for (Map.Entry<String, Integer> entry : csvContent.entrySet()) {
            Long actualCount = folderFileCounts.getOrDefault(entry.getKey(), 0L);
            System.out.println("Actual Count is: " + actualCount);

            if (!actualCount.equals(Long.valueOf(entry.getValue()))) {
                result.setStatus(false);
                result.setMsg("ファイル " + entry.getKey() + " のフレーム数とCSVが一致しません。期待される数: " + entry.getValue() + ", 実際の数: " + actualCount);
                return result;
            }
        }

        result.setStatus(true);
        result.setMsg("フレームの数とCSVが一致しています。");
        return result;
    }


    public List<CheckResult> checkFiles(CheckRequest checkRequest) {

        List<String> normalized = normalizeDirectoryPaths(checkRequest.getDirectories());
        Set<String> uniqueSub = sanitize(normalized);
        System.out.println("uniqueSub: " + uniqueSub);
        List<CheckResult> results = new ArrayList<>();

        Map<String, Integer> csvContent = checkRequest.getCsvData();
        CheckResult result1 = checkFileNumber(uniqueSub, csvContent);
        results.add(result1);
        if (!result1.isStatus()) {
            return results;
        }

        CheckResult result2 = checkFolderNames(uniqueSub, csvContent);
        results.add(result2);
        if (!result2.isStatus()) {
            return results;
        }

        CheckResult result3 = checkTextMatch(normalized, csvContent);
        results.add(result3);
        return results;

    }

    public Set<String> sanitize(List<String> normalizedDirectories) {

        System.out.println("normalizedDirectories is: " + normalizedDirectories);

        Set<String> uniqueSubdirs = new HashSet<>();

        for (String dir : normalizedDirectories) {
            String[] parts = dir.split("/");// Assuming directories are separated by forward slashes
            uniqueSubdirs.add(parts[0]);  // Assuming the subdirectory is the second part
        }
        return uniqueSubdirs;
    }

    public List<String> normalizeDirectoryPaths(List<String> originalDirectories) {

        return originalDirectories.stream()
                .map(s -> s.replace("\\", "/"))
                .collect(Collectors.toList());
    }
}