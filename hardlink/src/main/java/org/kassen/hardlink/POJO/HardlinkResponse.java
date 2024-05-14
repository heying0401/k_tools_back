package org.kassen.hardlink.POJO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HardlinkResponse {
    private int successCount;
    private int failureCount;
    private int totalCount;
    private List<String> failedVFXShots;
    private List<String> overwrittenFiles;

    public HardlinkResponse() {
        this.failedVFXShots = new ArrayList<>();
        this.overwrittenFiles = new ArrayList<>();
    }

    public void addFailedVFXShot(String vfxShot) {
        this.failedVFXShots.add(vfxShot);
    }
    public void addOverwrittenFile(String file) {
        this.overwrittenFiles.add(file);
    }

    public void incrementTotalCount() {
        totalCount++;
    }

    public void incrementSuccessCount() {
        this.successCount++;
    }

    public void incrementFailureCount() {
        this.failureCount++;
    }
}
