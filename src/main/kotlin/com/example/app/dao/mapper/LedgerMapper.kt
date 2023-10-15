package com.example.app.dao.mapper
import com.example.app.dao.po.LedgerPO
import org.apache.ibatis.annotations.Mapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
@Mapper
interface LedgerMapper : BaseMapper<LedgerPO> {
}
