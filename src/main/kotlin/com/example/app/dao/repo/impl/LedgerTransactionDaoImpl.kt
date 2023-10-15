package com.example.app.dao.repo.impl
import org.springframework.stereotype.Repository
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.app.dao.po.LedgerTransactionPO
import com.example.app.dao.mapper.LedgerTransactionMapper
import com.example.app.dao.repo.LedgerTransactionDao
@Repository
class LedgerTransactionDaoImpl : ServiceImpl<LedgerTransactionMapper,LedgerTransactionPO>() , LedgerTransactionDao {
}
