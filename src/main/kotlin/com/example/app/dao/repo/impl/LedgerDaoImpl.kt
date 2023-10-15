package com.example.app.dao.repo.impl
import org.springframework.stereotype.Repository
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.app.dao.po.LedgerPO
import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.repo.LedgerDao
@Repository
class LedgerDaoImpl : ServiceImpl<LedgerMapper,LedgerPO>() , LedgerDao {
}
