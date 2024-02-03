package com.example.app.dao.mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.example.app.dao.po.LedgerPO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface LedgerMapper : BaseMapper<LedgerPO> {
}
