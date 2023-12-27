package com.kassen.purchase.POJO;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;



import java.io.Serializable;

@Data
@TableName("PurchaseRequests")
public class PurchaseRequest{

    @TableId
    private Integer id;

    private String department;

    private String purchaseFrom;

    private String category;

    private String showName;

    private String name;

    private String amount;

    private String approvedBy;

    private String deliveryDate;

    private String purchaseSource;

    private String purchaseSourceUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

