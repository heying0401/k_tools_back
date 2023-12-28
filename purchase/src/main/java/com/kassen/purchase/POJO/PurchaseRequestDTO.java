package com.kassen.purchase.POJO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PurchaseRequestDTO {

    private Integer id;

    private String department;

    private String purchaseFrom;

    private String category;

    private String showName;

    private String item;

    private String amount;

    private String approvedBy;

    private String deliveryDate;

    private String purchaseSource;

    private String purchaseSourceUrl;

}

