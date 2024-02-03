package com.example.app.dao.mapper

import com.example.app.dao.*
import com.example.app.rest.SaveTranDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserMapperTest {

    @Autowired
    lateinit var userConfigMapper: UserConfigMapper

    @Autowired
    lateinit var userUserConfigMapper: UserUserConfigMapper

    @Test
    fun test_list_user_config() {
        println(userConfigMapper.selectList(null))
    }

    @Test
    fun test_list_user_user_config() {
        println(userUserConfigMapper.selectList(null))
    }

    @Test
    fun select_specific_user_config_test() {
        var result = userUserConfigMapper.getUserConfigByKey(1, "default_ledger")
        println(result)

        result = userUserConfigMapper.getUserConfigByKey(1, "default_ledge")
        println(result)
    }

    @Autowired
    lateinit var userLedgerMapper: UserLedgerMapper
    @Test
    fun select_specific_user_ledger_list_test() {
        userLedgerMapper.getUserLedgers(1).forEach {
            println(it)
        }
    }

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var userTransactionMapper: UserTransactionMapper

    @Test
    fun add_record_2_user () {
        userDao.addRecordToUser(SaveTranDTO().apply {
            categoryValue = "fasdfasdf"
            amount = "100"
            count = 1
            description = "default"
            location = mapOf()
        }, 1, "default")

        var list =
            userTransactionMapper.selectList(null)
        list.forEach(::println)
    }

    @Autowired
    lateinit var userRecordTypeMapper: UserRecordTypeMapper

    @Test
    fun list_user_record_types() {
        userRecordTypeMapper.getUserRecordByValue(1, "fruit").apply { println(this) }
        userRecordTypeMapper.getUserRecordTypes(1).forEach(::println)
    }

    @Autowired
    lateinit var transTypeMapper:TransactionCategoryMapper
    @Autowired
    lateinit var transactionMapper: TransactionMapper
    @Autowired
    lateinit var ledgerMapper: LedgerMapper


    @Autowired
    lateinit var userMapper: UserMapper

}
