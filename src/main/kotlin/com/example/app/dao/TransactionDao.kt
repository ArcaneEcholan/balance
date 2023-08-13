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
import java.math.BigDecimal

@TableName("`transaction`")
data class TransactionPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    var amount: BigDecimal? = null,
    var orderNo: String? = null,
    var categoryId: Long? = null,
    var description: String? = null,
    var locationId: Long? = null,
    var datetime: String? = null,
    var count: Int? = null
) : Serializable

@Mapper
interface TransactionMapper : BaseMapper<TransactionPO> {
}


interface TransactionDao : IBaseDao<TransactionPO> {
}

@Repository
class TransactionDaoImpl : BaseDao<TransactionMapper, TransactionPO>(), TransactionDao {
}
