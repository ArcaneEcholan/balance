package com.example.app.dao

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository
import java.io.Serializable

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
}


@TableName("`user_config`")
data class UserConfigPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    @TableField(value="`key`")
    var key: String? = null,
    @TableField(value="`value`")
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
