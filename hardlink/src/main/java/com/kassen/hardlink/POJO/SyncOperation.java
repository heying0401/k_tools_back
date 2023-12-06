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
    private Integer id;
    private String root;
    private String target;
    @EnumValue
    private SyncStatus status;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for status
    public enum SyncStatus {
        PENDING, PAUSED, IN_PROGRESS, STOPPED, FAILED
    }
}

