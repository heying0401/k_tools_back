package com.kassen.request.POJO;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("PurchaseRequests")
public class PurchaseRequest{

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String department;
    private String purchaseFrom;
    private String category;
    private String showName;
    private String item;
    private Integer amount;
    private String price;
    private String approvedBy;
    private String purchaseBy;
    private String purchaseSourceUrl;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

