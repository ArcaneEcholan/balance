package com.example.app.service

import org.junit.jupiter.api.Test
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TransactionServiceImplTest {
private val log = KotlinLogging.logger {}
    @Autowired
    lateinit var transactionService: TransactionService

    @Test
    fun list() {
        val list = transactionService.list(1, 10)
        log.debug { list }
    }

    @Test
    fun insert() {
        val transactionVO = transactionService.insert(
            amount = 100.toBigDecimal(),
            categoryId = 1,
            description = "test",
            datetime = "2021-01-01 00:00:00",
            location = mapOf(
                "province" to "广东省",
            )
        )
        log.debug { transactionVO }
    }
}