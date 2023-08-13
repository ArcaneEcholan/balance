package com.example.app.dao.utils.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.app.dao.utils.base.pagination.Page;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface IBaseDao<T> extends IService<T> {

    T existsByPrimaryKey(T entity);

    /**
     * 快捷分页方法。queryCondition携带分页查询条件信息，非空字段自动构造查询条件（空字段自动忽略）。如果queryCondition类中没有
     * 指定pageSize和pageNo，默认值为10和1.
     * <pre>
     * {@literal
     *
     *     // 定义 PageConfig 类
     *     @PageConfig
     *     class UserQueryConfig {
     *         @PageSize
     *         Long pageSize;
     *
     *         @PageNo
     *         Long pageNo;
     *
     *         String username;
     *
     *         @Condition(con = BaseDao.ConditionEnum.GE)
     *         Integer age;
     *     }
     *
     *     // Dao继承BaseDao
     *     public interface UserDao extends IBaseDao<UserPO> {
     *         // ...
     *     }
     *     @Repository
     *     public class UserDaoImpl extends BaseDao<UserMapper, UserPO> implements UserDao {
     *         // ...
     *     }
     *
     *     // 查询数据
     *     userDao.pageData(UserQueryConfig, null, (idx, po) -> {
     *         doSthToPo(po);
     *     });
     *
     *  }
     * </pre>
     * @param queryCondition 封装了查询条件
     * @param customCondition 自定义查询条件
     */
    Page<T> pageData(Object queryCondition, QueryWrapper<T> customCondition);

    Page<T> pageData(Object queryCondition, QueryWrapper<T> customCondition, BiConsumer<Integer, T> callback) ;

    <R> Page<R> pageData(Object queryCondition, QueryWrapper<T> customCondition, BiFunction<Integer, T, R> callback) ;

    // getOne
    T getOneByMap(String col1, Object val1);

    T getOneByMap(String col1, Object val1, String col2, Object val2);

    T getOneByMap(String col1, Object val1, String col2, Object val2,
                  String col3, Object val3);

    T getOneByMap(String col1, Object val1, String col2, Object val2,
                  String col3, Object val3, String col4, Object val4);

    // getList
    List<T> listByMap(String col1, Object val1);

    List<T> listByMap(String col1, Object val1, String col2, Object val2);

    List<T> listByMap(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3);

    List<T> listByMap(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3, String col4, Object val4);

    // update

    Integer updateByMap(String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String col2, Object val2, String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String setCol, Object setVal);

    Integer updateByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String col4, Object val4, String setCol, Object setVal);

    // remove
    Integer removeByMap(String col1, Object val1);

    Integer removeByMap(String col1, Object val1, String col2, Object val2);

    Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3);

    Integer removeByMap(String col1, Object val1, String col2, Object val2, String col3, Object val3, String col4, Object val4);

    enum Like{
        RIGHT,
        LEFT
    }

    Boolean fieldUnique(Long id, String fieldName, Object value);

    // like right
    List<T> like(String col1, Object val1, Like like);

    List<T> like(String col1, Object val1, String col2, Object val2, Like like);

    List<T> like(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3, Like like);

    List<T> like(String col1, Object val1, String col2, Object val2,
                      String col3, Object val3, String col4, Object val4, Like like);


}
