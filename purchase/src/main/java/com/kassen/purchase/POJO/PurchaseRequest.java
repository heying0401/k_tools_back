package com.kassen.purchase.POJO;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;


@Data
@TableName("PurchaseRequests")
public class PurchaseRequest{

    @TableId
    private Integer id;

    private String department;

    private String purchaseFrom;

    private String category;

    private String showName;

    private String item;

    private String amount;

    private String approvedBy;

    private String deliveryDate;

    private String purchaseSourceUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

