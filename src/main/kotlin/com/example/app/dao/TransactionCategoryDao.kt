package com.example.app.dao
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.app.dao.utils.base.BaseDao
import com.example.app.dao.utils.base.IBaseDao
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository
import java.io.Serializable


@Mapper
interface TransactionCategoryMapper : BaseMapper<TransactionCategoryPO> {
}

@TableName("`transaction_category`")
data class TransactionCategoryPO (
    @TableId(value="id", type= IdType.AUTO)
    var id: Long ?,
    var value: String ?,
    var icon: String ?
)
    : Serializable

interface TransactionCategoryDao : IBaseDao<TransactionCategoryPO> {
}
@Repository
class TransactionCategoryDaoImpl : BaseDao<TransactionCategoryMapper, TransactionCategoryPO>() ,
    TransactionCategoryDao {
}
