package com.kassen.request.POJO;

import lombok.Data;

@Data
public class PurchaseRequestDTO {

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
}

