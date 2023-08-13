package com.example.app.dao

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.example.app.dao.utils.base.BaseDao
import com.example.app.dao.utils.base.IBaseDao
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Repository
import java.io.Serializable

interface ToVO<V> {
    fun toVO(): V
}

@Mapper
interface LocationMapper : BaseMapper<LocationPO> {
}

@TableName("`location`")
data class LocationPO(
    @TableId(value = "id", type = IdType.AUTO)
    var id: Long? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var formattedName: String? = null,
    var province: String? = null,
    var city: String? = null,
    var district: String? = null,
    var adcode: String? = null,
    var citycode: String? = null,

    var streetDirection: String? = null,
    var streetDistance: String? = null,
    var streetLocation: String? = null,
    var streetName: String? = null,
    var streetNumber: String? = null,
    var towncode: String? = null,
    var township: String? = null,
) : Serializable {
    fun allNull(): Boolean {
        return latitude == null
                && longitude == null
                && formattedName == null
                && province == null
                && city == null
                && district == null
                && adcode == null
                && citycode == null
                && streetDirection == null
                && streetDistance == null
                && streetLocation == null
                && streetName == null
                && streetNumber == null
                && towncode == null
                && township == null
    }

}


interface LocationDao : IBaseDao<LocationPO> {
}

@Repository
class LocationDaoImpl : BaseDao<LocationMapper, LocationPO>(), LocationDao {
}


