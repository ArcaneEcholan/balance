package com.example.app.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


public class EscapedQueryWrapper<T> extends QueryWrapper<T> {

    @Override
    public QueryWrapper<T> eq(String column, Object val) {
        return super.eq(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> ne(String column, Object val) {
        return super.ne(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> gt(String column, Object val) {
        return super.gt(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> ge(String column, Object val) {
        return super.ge(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> lt(String column, Object val) {
        return super.lt(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> le(String column, Object val) {
        return super.le(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> between(String column, Object val1, Object val2) {
        return super.between(column, val1, val2);
    }

    @Override
    public QueryWrapper<T> notBetween(String column, Object val1, Object val2) {
        return super.notBetween(column, val1, val2);
    }

    @Override
    public QueryWrapper<T> like(String column, Object val) {
        return super.like(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> notLike(String column, Object val) {
        return super.notLike(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> likeLeft(String column, Object val) {
        return super.likeLeft(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    @Override
    public QueryWrapper<T> likeRight(String column, Object val) {
        return super.likeRight(MyBatisUtil.INSTANCE.escapeColumnName(column), val);
    }

    
}
