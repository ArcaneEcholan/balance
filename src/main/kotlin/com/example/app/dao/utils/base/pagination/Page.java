package com.example.app.dao.utils.base.pagination;

import java.util.List;

public
class Page<T> {
    public Long pageSize;
    public Long pageNo;
    public List<T> records;
    public Long total;

    public Page() {
    }

    public Page(
            Long pageSize,
            Long pageNo,
            List<T> records,
            Long total
    ) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.records = records;
        this.total = total;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}