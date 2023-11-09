package com.kassen.hardlink.POJO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Data
@TableName("sync_operations")
public class SyncOperation {

    private Long id;
    private String rootPath;
    private String targetPath;
    private SyncStatus status;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for status
    public enum SyncStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}

