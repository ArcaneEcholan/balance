package com.example.app.dao.po

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable

@TableName("`ledger`")
data class LedgerPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long?,
    var name: String?,
    var ctime: String?,
) : Serializable {
    // default
    constructor() : this(null, null, null)
}
