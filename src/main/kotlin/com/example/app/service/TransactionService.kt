package com.example.app.service

import cn.hutool.core.date.DateTime
import com.example.app.dao.*
import com.example.app.dao.utils.base.pagination.Page
import com.example.app.dao.utils.base.pagination.PageConfig
import com.example.app.dao.utils.base.pagination.PageNo
import com.example.app.dao.utils.base.pagination.PageSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

//    var id: Long ?,
//     var amount: BigDecimal?,
//     var orderNo: String ?,
//     var categoryId: Long ?,
//     var description: String ?,
//     var locationId: Long ?,
//     var datetime: String ?
class TransactionVO {
    var id: Long? = null
    var amount: BigDecimal? = null
    var orderNo: String? = null
    var categoryId: Long? = null
    var description: String? = null
    var locationId: Long? = null
    var datetime: String? = null

    var categoryValue: String? = null
    var location: Any? = null

    override fun toString(): String {
        return "TransactionVO(id=$id, amount=$amount, orderNo=$orderNo, categoryId=$categoryId, description=$description, locationId=$locationId, datetime=$datetime, categoryValue=$categoryValue, location=$location)"
    }

}

interface TransactionService {

    fun fillVO(transaction: TransactionPO): TransactionVO

    fun list(pageNo: Long, pageSize: Long): List<TransactionVO>

    // var id: Long?,
    //     var amount: BigDecimal?,
    //     var orderNo: String?,
    //     var categoryId: Long?,
    //     var description: String?,
    //     var locationId: Long?,
    //     var datetime: String?,
    fun insert(
        amount: BigDecimal,
        categoryId: Long,
        count: Int,
        description: String?,
        datetime: String,
        location: Map<String, String>,
    ): TransactionVO
}

@PageConfig
class PageCondition {
    @PageNo
    var pageNo: Long? = null

    @PageSize
    var pageSize: Long? = null
}

@Service
class TransactionServiceImpl : TransactionService {

    @Autowired
    lateinit var transactionDao: TransactionDao


    @Autowired
    lateinit var categoryDao: TransactionCategoryDao

    @Autowired
    lateinit var locationDao: LocationDao

    override fun fillVO(transaction: TransactionPO): TransactionVO {
        var vo = TransactionVO()
        vo.id = transaction.id
        vo.amount = transaction.amount
        vo.orderNo = transaction.orderNo
        vo.categoryId = transaction.categoryId
        vo.description = transaction.description
        vo.locationId = transaction.locationId
        vo.datetime = transaction.datetime

        var location = locationDao.getOneByMap(
            "id", transaction.locationId
        )
        vo.location = location

        var category = categoryDao.getOneByMap(
            "id", transaction.categoryId
        )
        vo.categoryValue = category?.value
        return vo
    }


    override fun list(pageNo: Long, pageSize: Long): List<TransactionVO> {
        var pageCondition = PageCondition()
        val pageData: Page<TransactionPO> = transactionDao.pageData(pageCondition, null)
        var data = pageData.records

        data.map {
            fillVO(it)

        }.toList().apply { return this }
    }

    override fun insert(
        amount: BigDecimal,
        categoryId: Long,
        count: Int,
        description: String?,
        datetime: String,
        locationInformationMap: Map<String, String>,
    ): TransactionVO {

        var location = LocationPO(
            formattedName = locationInformationMap?.get("formattedName"),
            latitude = locationInformationMap?.get("latitude"),
            longitude = locationInformationMap?.get("longitude"),
            province = locationInformationMap?.get("province"),
            city = locationInformationMap?.get("city"),
            district = locationInformationMap?.get("district"),
            adcode = locationInformationMap?.get("adcode"),
            citycode = locationInformationMap?.get("citycode"),

            streetName = locationInformationMap?.get("streetName"),
            streetNumber = locationInformationMap?.get("streetNumber"),
            streetDirection = locationInformationMap?.get("streetDirection"),
            streetDistance = locationInformationMap?.get("streetDistance"),
            streetLocation = locationInformationMap?.get("streetLocation"),

            towncode = locationInformationMap?.get("towncode"),
            township = locationInformationMap?.get("township"),
        )

        if(!location.allNull()) {
            locationDao.save(location)
        }

        var transaction = TransactionPO(
            amount = amount,
            count = count,
            categoryId = categoryId,
            description = description,
            datetime = DateTime.now().toString(),
            locationId = location.id,
            orderNo = UUID.randomUUID().toString()
        )

        transactionDao.save(transaction)

        return fillVO(transaction)
    }
}
