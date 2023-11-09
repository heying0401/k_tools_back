package com.kassen.hardlink.POJO;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("sync_operations")
public class SyncOperation {

    @TableId
    private Long id;
    private String rootPath;
    private String targetPath;
    @EnumValue
    private SyncStatus status;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for status
    public enum SyncStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}

