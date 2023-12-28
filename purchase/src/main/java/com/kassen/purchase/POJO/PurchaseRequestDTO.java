package com.kassen.purchase.POJO;

import lombok.Data;

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

