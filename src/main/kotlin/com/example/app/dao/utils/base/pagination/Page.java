package com.example.app.dao.utils.base.pagination;

import lombok.Data;

import java.util.List;

@Data
public
class Page<T> {
    public Long pageSize;
    public Long pageNo;
    public List<T> records;
    public Long total;
}