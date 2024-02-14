package org.kassen.shotlist.Service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.IOUtils;


@Service
public class ShotlistService {

    public String processDirectory(String directoryPath) throws IOException, InterruptedException {
        File dir = new File(directoryPath);
        File[] videoFiles = dir.listFiles((d, name) -> name.endsWith(".mp4") || name.endsWith(".mov")); // Adjust as needed
        String thumbnailDirPath = directoryPath + "_thumbnails";

        for (File videoFile : videoFiles) {
            extractThumbnail(videoFile.getAbsolutePath(), thumbnailDirPath);
        }

        File thumbnailDir = new File(thumbnailDirPath);
        File[] thumbnails = thumbnailDir.listFiles();
        List<String> thumbnailPaths = Arrays.stream(thumbnails).map(File::getAbsolutePath).collect(Collectors.toList());

        String parentDirPath = new File(directoryPath).getParent();
        String spreadsheetPath;
        // Fallback in case the directoryPath is at the root (no parent directory)
        spreadsheetPath = Objects.requireNonNullElse(parentDirPath, directoryPath) + "/shotlist.xlsx";

        return generateSpreadsheet(thumbnailPaths, spreadsheetPath);
    }


    public void extractThumbnail(String videoFilePath, String thumbnailDirPath) throws IOException, InterruptedException {
        File thumbnailDir = new File(thumbnailDirPath);
        if (!thumbnailDir.exists()) {
            boolean wasSuccessful = thumbnailDir.mkdirs();
            if (!wasSuccessful) {
                throw new IOException("Failed to create directory for thumbnails");
            }
        }

        String thumbnailFilePath = thumbnailDirPath + "/" + new File(videoFilePath).getName().replaceAll("\\..+$", ".jpg");

        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg", "-i", videoFilePath, "-vf",
                "thumbnail,scale='if(gt(a,1),250,-1)':'if(gt(a,1),-1,250)':force_original_aspect_ratio=decrease",
                "-vframes", "1", thumbnailFilePath
        );
        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Failed to extract thumbnail for video: " + videoFilePath);
        }
    }

    public String generateSpreadsheet(List<String> thumbnailPaths, String outputPath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Thumbnails");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Shot Name");
            headerRow.createCell(1).setCellValue("Thumbnail");

            // Setting the column width to accommodate ~250 pixels
            // Assuming ~7 pixels per character, but this is an approximation
            int approxCharWidth = (int) Math.round(250.0 / 7);
            sheet.setColumnWidth(1, approxCharWidth * 256); // Excel measures column width in 1/256th of a character width

            int rowNum = 1;
            for (String thumbnailPath : thumbnailPaths) {
                Row row = sheet.createRow(rowNum);
                String shotName = new File(thumbnailPath).getName().replaceFirst("[.][^.]+$", ""); // Remove file extension
                row.createCell(0).setCellValue(shotName);

                // Assuming a thumbnail height of 250px, converting to points for row height
                // 72 points per inch, with a rough approximation of 96 pixels per inch
                double pointsPerInch = 72;
                double pixelsPerInch = 96;
                double thumbnailHeightInPoints = (250.0 / pixelsPerInch) * pointsPerInch;
                row.setHeightInPoints((short)thumbnailHeightInPoints);

                // Load the thumbnail as a byte array
                InputStream inputStream = new FileInputStream(thumbnailPath);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                inputStream.close();

                CreationHelper helper = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();

                anchor.setCol1(1); // Column B
                anchor.setRow1(rowNum); // Current row
//                anchor.setCol2(2); // End Column
//                anchor.setRow2(rowNum + 1); // End Row

                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize();

                sheet.setColumnWidth(1, 20 * 256);
                row.setHeightInPoints(120);

                rowNum++; // Move to the next row
            }

            // Autosize the first column to fit the shot names
            sheet.autoSizeColumn(0);

            // Write the output to a file
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
        return outputPath;
    }


}
