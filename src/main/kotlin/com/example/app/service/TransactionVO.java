package com.example.app.service;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransactionVO {
    public Long id;
    public BigDecimal amount;
    public String orderNo;
    public Long categoryId;
    public String description;
    public Long locationId;
    public String datetime;
    public Integer count;
    public String categoryValue;
    public Object location;
}