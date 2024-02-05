package com.example.app.dao

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.example.app.rest.SaveTranDTO
import com.example.app.service.TransactionService
import com.example.app.utils.DateTime
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.io.Serializable
import java.math.BigDecimal

@TableName("`user`")
data class UserPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    var username: String? = null,
    var password: String? = null,
    var ctime: String? = null,
) : Serializable {
    // default
    constructor() : this(null, null, null, null)
}

@Mapper
interface UserMapper : BaseMapper<UserPO> {
}

@Repository
class UserDao {

    @Autowired
    lateinit var categoryDao: TransactionCategoryDao

    @Autowired
    lateinit var transactionService: TransactionService

    fun addRecordToUser(saveTranDTO: SaveTranDTO, userId: Long, ledgerName: String) {
        var cateId = categoryDao.getOneByMap("value", saveTranDTO.categoryValue)?.id

        var tran = transactionService.insert(
            ledgerName = ledgerName,
            amount = BigDecimal(saveTranDTO.amount),
            categoryId = cateId ?: 0,
            count = saveTranDTO.count!!,
            description = saveTranDTO.description,
            datetime = DateTime.now().toString(),
            // leave location as empty, support it later
            location = saveTranDTO.location!!
        )
    }
}


@TableName("`user_config`")
data class UserConfigPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    @TableField(value = "`key`")
    var key: String? = null,
    @TableField(value = "`value`")
    var value: String? = null,
) : Serializable {
    // default
    constructor() : this(null, null, null)
}

@Mapper
interface UserConfigMapper : BaseMapper<UserConfigPO> {
}

@Repository
class UserConfigDao {
}


@TableName("`user_user_config`")
data class UserUserConfigPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long? = null,
    var userConfigId: Long? = null,
) : Serializable {
    // default
    constructor() : this(null, null, null)
}

@Mapper
interface UserUserConfigMapper : BaseMapper<UserUserConfigPO> {
    fun getUserConfigByKey(@Param("userId") userId: Long, @Param("key") key: String): UserConfigMapperResult?
}

@Repository
class UserUserConfigDao {
}

class UserConfigMapperResult {
    var key: String? = null
    var value: String? = null
    var userId: Long? = null
    var userConfigId: Long? = null
    override fun toString(): String {
        return "UserConfigMapperResult(key=$key, value=$value, userId=$userId, userConfigId=$userConfigId)"
    }
}


@TableName("`user_ledger`")
data class UserLedgerPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long? = null,
    var ledgerId: Long? = null,
) : Serializable {
    // default
    constructor() : this(null, null, null)
}

@Mapper
interface UserLedgerMapper : BaseMapper<UserLedgerPO> {
    fun getUserLedgers(@Param("userId") userId: Long): MutableList<UserLedgerMapperResult>
}

@Repository
class UserLedgerDao {
}

class UserLedgerMapperResult {
    var userId: Long? = null
    var username: String? = null
    var ledgerId: Long? = null
    var ledgerName: String? = null

    override fun toString(): String {
        return "UserLedgerMapperResult(userId=$userId, username=$username, ledgerId=$ledgerId, ledgerName=$ledgerName)"
    }
}






@TableName("`user_record_type`")
data class UserRecordTypePO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long? = null,
    var typeId: Long? = null,
) : Serializable {
    // default
    constructor() : this(null, null, null)
}

@Mapper
interface UserRecordTypeMapper : BaseMapper<UserRecordTypePO> {
    fun getUserRecordTypes(@Param("userId") userId: Long): MutableList<UserRecordTypeMapperResult>

    fun getUserRecordTypeByValue(@Param("userId") userId: Long, @Param("value") typeValue: String): UserRecordTypeMapperResult?
}

@Repository
class UserRecordTypeDao {
}

class UserRecordTypeMapperResult {
    var typeId: Long? = null
    var typeIcon: String? = null
    var typeValue: String? = null

    // tostring
    override fun toString(): String {
        return "UserRecordTypeMapperResult(typeId=$typeId, typeIcon=$typeIcon, typeValue=$typeValue)"
    }

}
