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

    public HardlinkResponse() {
        this.failedVFXShots = new ArrayList<>(); // Initialize the list here to prevent NullPointerException.
    }

    public void addFailedVFXShot(String vfxShot) {
        this.failedVFXShots.add(vfxShot);
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
