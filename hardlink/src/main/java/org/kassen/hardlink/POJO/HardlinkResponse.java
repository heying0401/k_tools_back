package org.kassen.hardlink.POJO;

import lombok.Data;

import java.util.List;

@Data
public class HardlinkResponse {
    private int successCount;
    private int failureCount;
    private List<String> failedVFXShots;

    public void addFailedVFXShot(String vfxShot) {
        this.failedVFXShots.add(vfxShot);
    }

    public void incrementSuccessCount() {
        this.successCount++;
    }

    public void incrementFailureCount() {
        this.failureCount++;
    }
}
