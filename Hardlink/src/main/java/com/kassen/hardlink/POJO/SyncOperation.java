package com.kassen.hardlink.POJO;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Data
public class SyncOperation {

    private Long id;
    private String rootPath;
    private String targetPath;
    private SyncStatus status;
    private Integer durationSeconds;
    private OffsetDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for status
    public enum SyncStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}

