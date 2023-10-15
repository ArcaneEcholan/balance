package com.example.app.dao.po
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.io.Serializable
@TableName("`ledger_transaction`")
data class LedgerTransactionPO (
    @TableId(value="id", type=IdType.AUTO)
    var id: Long ?,
    var ledgerId: Long ?,
    var transactionId: Long ?
)
: Serializable
