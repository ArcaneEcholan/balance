package com.example.app.dao.mapper

import com.example.app.dao.UserConfigMapper
import com.example.app.dao.UserUserConfigMapper
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

}
