package com.example.app.rest

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class TransControllerTest {

    @Autowired
    lateinit var transController: TransController

    @Test
    fun getTransactions() {
        val transactions = transController.getTransactions("2023-2")
        transactions.body!!.forEach {
            println(it)
        }
    }
}