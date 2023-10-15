package com.example.app.dao.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.app.dao.po.LedgerTransactionPO;
import com.example.app.service.TransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface LedgerTransactionMapper : BaseMapper<LedgerTransactionPO> {
        fun selectTransactionsOfSpecificLedger(
                @Param("ledgerId") ledgerId: Long,
                @Param("startDate") startDate: String,
                @Param("endDate") endDate: String,
        ): List<TransactionVO>
}
