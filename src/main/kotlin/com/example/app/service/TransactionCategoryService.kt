package com.example.app.service

import com.example.app.dao.TransactionCategoryDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface TransactionCategoryService {
    fun getAll(): List<TransactionCategoryVO>
}

data class TransactionCategoryVO(
    val id: Long?,
    val value: String?,
)
@Service
class TransactionCategoryServiceImpl : TransactionCategoryService {
    @Autowired
    lateinit var categoryDao: TransactionCategoryDao

    override fun getAll(): List<TransactionCategoryVO> {
        return categoryDao.list().map {
            TransactionCategoryVO(it.id, it.value)
        }
    }
}
