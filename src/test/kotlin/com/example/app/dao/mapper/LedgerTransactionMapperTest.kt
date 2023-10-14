package com.example.app.dao.mapper;

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LedgerTransactionMapperTest {

    @Autowired
    lateinit var ledgerTransactionMapper: LedgerTransactionMapper

    @Test
    fun test() {
        var a =  ledgerTransactionMapper.selectTransactionsOfSpecificLedger(
                2L,
        "2023-08-01 11:11:11",
        "2023-08-20 11:11:11"
        )

        a.forEach{
            println(it)
        }

    }

}
