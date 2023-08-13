package com.example.app.dao.utils.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.app.dao.utils.base.pagination.*;
import com.example.app.utils.MyBatisUtilsKt;
import com.example.app.utils.VarCaseConvertUtils;
import com.example.app.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public class BaseDao<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseDao<T> {

    @Override
    public T existsByPrimaryKey(T entity) {
        Class<?> entityClass = entity.getClass();
        Field[] declaredFields = entityClass.getDeclaredFields();
        Map<String, Object> primaryKeyValues = new HashMap<>();
        Object priKeyValue;
        for (Field declaredField : declaredFields) {
            log.debug("检查类：{} 的 {} 字段是否是主键字段", entityClass.getName(), declaredField.getName());
            declaredField.setAccessible(true);
            PrimaryKey primaryKeyAnno = declaredField.getAnnotation(PrimaryKey.class);
            if (primaryKeyAnno != null) {
                log.debug("类：{} 的 {} 字段是主键字段", entityClass.getName(), declaredField.getName());
                try {
                    priKeyValue = declaredField.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                log.debug("获取主键字段的值为：{}", priKeyValue);
                String dbFieldName = VarCaseConvertUtils.lowerCamel2LowerUnderScore(declaredField.getName());
                primaryKeyValues.put(dbFieldName, priKeyValue);
                log.debug("参数：{} => {} 放入map中", dbFieldName, priKeyValue);
            }
        }
        log.debug("实体类：{} 所有主键字段的值集合：{}", entityClass.getName(), primaryKeyValues);

        log.debug("构造查询条件");
        QueryWrapper<T> queryWrapper = constructQueryConditionByMap(primaryKeyValues);
        log.debug("执行查询");

        T oneEntityFromDB = this.getOne(queryWrapper, false);
        log.debug("主键查询操作结果：{}", oneEntityFromDB);
        return oneEntityFromDB;
    }

    private QueryWrapper<T> constructQueryConditionByMap(Map<String, Object> primaryKeyValues) {
        QueryWrapper<T> queryWrapper = MyBatisUtilsKt.create();;
        primaryKeyValues.forEach((dbFieldName, priKeyValue) -> {
            if (priKeyValue == null) {
                log.warn("主键字段：{} 的值为null，将不作为查询条件", dbFieldName);
            } else {
                log.debug("添加查询条件：{}={}", dbFieldName, priKeyValue);
                queryWrapper.eq(dbFieldName, priKeyValue);
            }
        });
        log.debug("最终查询条件：{}", queryWrapper);
        return queryWrapper;
    }

    public enum ConditionEnum {
        EQ,
        GT,
        LT, GE, LE, LIKE, RIGHT_LIKE, LEFT_LIKE, BETWEEN
    }

    /**
     * 将一个字段构造到指定的QueryWrapper中
     *
     * @param queryWrapper   目标QueryWrapper
     * @param conditionAnno  @Condition注解
     * @param value1         查询条件值
     * @param value2         查询条件值（有些查询方式有两个值，比如between）
     * @param defaultColName 如果@Condition注解为空，使用该值作为默认的查询字段名
     */
    public void condition(QueryWrapper<T> queryWrapper,
                          Condition conditionAnno, Object value1, Object value2,
                          String defaultColName) {
        String colName;
        // 如果@Condition为空，那么使用默认的字段名称和‘=’构造查询条件
        if (conditionAnno == null) {
            colName = VarCaseConvertUtils.lowerCamel2LowerUnderScore(defaultColName);
            // 默认使用 = 作为查询条件
            queryWrapper.eq(colName, value1);
            return;
        }

        // 否则使用注解指定的字段名称和查询方式
        colName = "".equals(conditionAnno.dbFieldName())
                ? VarCaseConvertUtils.lowerCamel2LowerUnderScore(defaultColName)
                : conditionAnno.dbFieldName();

        log.debug("Use \" {} {} {} \" as query condition", colName, conditionAnno.con(), value1);
        switch (conditionAnno.con()) {
            case EQ:
                queryWrapper.eq(colName, value1);
                break;
            case GT:
                queryWrapper.gt(colName, value1);
                break;
            case LT:
                queryWrapper.lt(colName, value1);
                break;
            case GE:
                queryWrapper.ge(colName, value1);
                break;
            case LE:
                queryWrapper.le(colName, value1);
                break;
            case LIKE:
                queryWrapper.like(colName, value1);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(colName, value1);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(colName, value1);
                break;
            // 暂时用不上
            case BETWEEN:
                queryWrapper.between(colName, value1, value2);
                break;
        }
    }

    private Page<T> doPage(Object conditionObj, QueryWrapper<T> customCondition) {
        Class<?> conditionObjClass = conditionObj.getClass();
        PageConfig annotation = conditionObjClass.getAnnotation(PageConfig.class);
        if (annotation == null) {
            log.error("分页实体类：{}， 没有标注PageConfig注解", conditionObjClass.getName());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

        Page<T> page = new Page<>();

        // 构造分页查询条件
        if (customCondition == null) {
            customCondition = MyBatisUtilsKt.create();
        }
        constructQueryCondition(conditionObj, customCondition, page);

        long pageNo = page.getPageNo();
        long pageSize = page.getPageSize();

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> mybatisPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        mybatisPage.setCurrent(pageNo);
        mybatisPage.setSize(pageSize);
        this.page(mybatisPage, customCondition);

        List<T> records = mybatisPage.getRecords();
        page.setTotal(mybatisPage.getTotal());
        page.setRecords(records);
        return page;
    }

    /**
     * 遍历queryCondition中的所有字段，构造一个QueryWrapper
     *
     * @param queryCondition  待扫描的对象
     * @param customCondition 目标QueryWrapper，可以在传入前添加额外的查询条件，不能为null
     * @param page            分页对象，该方法将设置分页大小和页数
     */
    private void constructQueryCondition(Object queryCondition, @NotNull QueryWrapper<T> customCondition,
                                         Page<T> page) {

        Class<?> conditionPageClass = queryCondition.getClass();
        Field[] conditionFields = conditionPageClass.getDeclaredFields();
        for (Field field : conditionFields) {
            field.setAccessible(true);

            // 获取字段值
            Object conditionValue = null;
            try {
                conditionValue = field.get(queryCondition);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            // 标记该字段是否是查询条件字段
            boolean conditionField = true;

            // 如果该字段标注了PageSize注解且不为空，那么该字段的值作为pageSize
            PageSize pageSizeAnno = field.getAnnotation(PageSize.class);
            if (pageSizeAnno != null) {
                conditionField = false;
                if (conditionValue != null && field.getType().equals(Long.class)) {
                    page.setPageSize((Long) conditionValue);
                    log.debug("检测到分页大小：{}", conditionValue);
                }
                else {
                    page.setPageSize(10L);
                }
            }

            // 如果该字段标注了PageSize注解且不为空，那么该字段的值作为pageSize
            PageNo pageNoAnno = field.getAnnotation(PageNo.class);
            if (pageNoAnno != null) {
                conditionField = false;
                if (conditionValue != null && field.getType().equals(Long.class)) {
                    page.setPageNo((Long) conditionValue);
                    log.debug("检测到分页页数：{}", conditionValue);
                }
                else {
                    page.setPageNo(1L);
                }
            }

            // 如果该字段不是条件字段，跳过
            if (!conditionField) {
                continue;
            }

            // 如果该字段为空，跳过
            if (conditionValue == null) {
                continue;
            }

            // 开始构造查询条件
            Condition conditionAnno = field.getAnnotation(Condition.class);
            condition(customCondition, conditionAnno, conditionValue, null, field.getName());
        }

        // 如果没有标注PageSize和PageNo的字段，使用默认值
        if (page.getPageSize() == null) {
            page.setPageSize(10L);
        }
        if (page.getPageNo() == null) {
            page.setPageNo(1L);
        }
    }

    @Override
    public Page<T> pageData(Object queryCondition, QueryWrapper<T> customCondition) {
        return this.pageData(queryCondition, customCondition, (idx, row) -> {
        });
    }

    @Override
    public Page<T> pageData(Object queryCondition, QueryWrapper<T> customCondition, BiConsumer<Integer, T> callback) {
        Page<T> page = this.doPage(queryCondition, customCondition);
        List<T> records = page.getRecords();
        // 处理查询后的结果
        if (callback != null) {
            for (int i = 0; i < records.size(); i++) {
                callback.accept(i, records.get(i));
            }
        }
        return page;
    }

    @Override
    public <R> Page<R> pageData(Object queryCondition, QueryWrapper<T> customCondition,
                                BiFunction<Integer, T, R> mapCallback) {
        Page<T> page = this.doPage(queryCondition, customCondition);
        List<T> records = page.getRecords();
        List<R> mapRecords = new ArrayList<>();
        if (mapCallback != null) {
            for (int i = 0; i < records.size(); i++) {
                mapRecords.add(mapCallback.apply(i, records.get(i)));
            }
            mapRecords = mapRecords.stream().filter((r) -> r != null).collect(Collectors.toList());
        }
        Page<R> rPage = new Page<>();
        rPage.setRecords(mapRecords);
        rPage.setTotal(page.getTotal());
        return rPage;
    }

    // getOne
    @Override
    public T getOneByMap(String col1, Object val1) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1);
        T one = this.getOne(eq, false);
        return one;
    }

    @Override
    public T getOneByMap(String col1, Object val1, String col2, Object val2) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2);
        T one = this.getOne(eq, false);
        return one;
    }

    @Override
    public T getOneByMap(String col1, Object val1, String col2, Object val2,
                         String col3, Object val3) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3);
        T one = this.getOne(eq, false);
        return one;
    }

    @Override
    public T getOneByMap(String col1, Object val1, String col2, Object val2,
                         String col3, Object val3, String col4, Object val4) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).eq(col4, val4);
        T one = this.getOne(eq, false);
        return one;
    }

    // getList
    @Override
    public List<T> listByMap(String col1, Object val1) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public List<T> listByMap(String col1, Object val1, String col2, Object val2) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public List<T> listByMap(String col1, Object val1, String col2, Object val2,
                             String col3, Object val3) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public List<T> listByMap(String col1, Object val1, String col2, Object val2,
                             String col3, Object val3, String col4, Object val4) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = tQueryWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).eq(col4, val4);
        List<T> list = this.list(eq);
        return list;
    }

    @Override
    public Integer updateByMap(String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    // update
    @Override
    public Integer updateByMap(String col1, Object val1, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    @Override
    public Integer updateByMap(String col1, Object val1, String col2, Object val2, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).eq(col2, val2).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    @Override
    public Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3,
                               String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).set(setCol, setVal);
        return this.baseMapper.update(null, eq);
    }

    @Override
    public Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3,
                               String col4, Object val4, String setCol, Object setVal) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<T> eq = updateWrapper.eq(col1, val1).eq(col2, val2).eq(col3, val3).eq(col4, val4).set(setCol,
                setVal);
        return this.baseMapper.update(null, eq);
    }

    // remove
    @Override
    public Integer removeByMap(String col1, Object val1) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Integer removeByMap(String col1, Object val1, String col2, Object val2) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        map.put(col2, val2);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        map.put(col2, val2);
        map.put(col3, val3);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3,
                               String col4, Object val4) {
        Map<String, Object> map = new HashMap<>();
        map.put(col1, val1);
        map.put(col2, val2);
        map.put(col3, val3);
        map.put(col4, val4);
        return this.baseMapper.deleteByMap(map);
    }

    @Override
    public Boolean fieldUnique(Long id, String fieldName, Object value) {
        QueryWrapper<T> queryWrapper = MyBatisUtilsKt.create();;
        queryWrapper.ne("id", id).eq(fieldName, value);
        T one = this.getOne(queryWrapper);
        if (one != null) {
            return false;
        }
        return true;
    }

    @Override
    public List<T> like(String col1, Object val1, Like like) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1);
        } else {
            eq = tQueryWrapper.likeLeft(col1, val1);
        }

        return this.list(tQueryWrapper);
    }

    @Override
    public List<T> like(String col1, Object val1, String col2, Object val2, Like like) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1).likeRight(col2, val2);
        } else {
            eq = tQueryWrapper.likeLeft(col1, val1).likeLeft(col2, val2);
        }

        return this.list(tQueryWrapper);
    }

    @Override
    public List<T> like(String col1, Object val1, String col2, Object val2,
                        String col3, Object val3, Like like) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1).likeRight(col2, val2).likeRight(col3, val3);
        }
        else {
            eq = tQueryWrapper.likeLeft(col1, val1).likeLeft(col2, val2).likeLeft(col3, val3);
        }

        return this.list(tQueryWrapper);
    }

    @Override
    public List<T> like(String col1, Object val1, String col2, Object val2,
                        String col3, Object val3, String col4, Object val4, Like like) {
        QueryWrapper<T> tQueryWrapper = MyBatisUtilsKt.create();;
        QueryWrapper<T> eq = null;
        if (like.equals(Like.RIGHT)) {
            eq = tQueryWrapper.likeRight(col1, val1).likeRight(col2, val2).likeRight(col3, val3).likeRight(col4, val4);
        }
        else {
            eq = tQueryWrapper.likeLeft(col1, val1).likeLeft(col2, val2).likeLeft(col3, val3).likeLeft(col4, val4);
        }

        return this.list(tQueryWrapper);
    }

}