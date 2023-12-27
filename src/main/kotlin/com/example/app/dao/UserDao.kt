package com.example.app.dao

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
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
