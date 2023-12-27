package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.UserMapper
import com.example.app.dao.UserPO
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.DAY
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.HOUR
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.MINUTE
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.SECOND
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.WEEK
import com.example.app.rest.JwtUtils.Companion.genToken
import io.jsonwebtoken.*
import lombok.extern.slf4j.Slf4j
import org.passay.CharacterData
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.validation.constraints.NotEmpty

@RestController
@Validated
@RequestMapping("/api")
class UserController {

    @Autowired
    lateinit var userDao: UserMapper

    class UserPasswordSetRequest {
        @NotEmpty
        var username: String? = null
    }

    @PostMapping("/user/password_set")
    fun isUserPasswordSet(@RequestBody userPasswordSetRequest: UserPasswordSetRequest): Any {
        var username = userPasswordSetRequest.username!!;
        var exists = userDao.exists(QueryWrapper<UserPO>().eq("username", username))
        return object {
            var set = exists
        }
    }


    class UserAuthRequest {
        @NotEmpty
        var username: String? = null

        @NotEmpty
        var password: String? = null
    }

    @PostMapping("/user/auth")
    fun auth(@RequestBody userAuthRequest: UserAuthRequest): Any {
        var username = userAuthRequest.username!!;
        var password = userAuthRequest.password!!;
        var exists = userDao.selectOne(
            QueryWrapper<UserPO>().eq("username", username)
                .eq("password", password)
        )

        if (exists == null) {
            return ResponseEntity.internalServerError().body(object {
                var message = "user not exists"
            })
        }
        var token = genToken(exists.id.toString(), EntityType.WEB_USER)
        return object {
            var token = token;
        }
    }

    class GenerateUserRequest {
        @NotEmpty
        var username: String? = null
    }

    @PostMapping("/user/generating")
    fun generateUser(@RequestBody generateUserRequest: GenerateUserRequest): Any {
        var username = generateUserRequest.username!!;
        var exists = userDao.exists(QueryWrapper<UserPO>().eq("username", username))
        if (exists) {
            return ResponseEntity.internalServerError().body(object {
                var message = "user already exists"
            })
        }

        var user = UserPO()
        user.username = username
        user.password = generatePassayPassword()
        userDao.insert(user)

        var token = genToken(user.id.toString(), EntityType.WEB_USER)
        return object {
            var token = token;
        }
    }

}

fun generatePassayPassword(): String {
    val gen = PasswordGenerator()
    val lowerCaseChars: CharacterData = EnglishCharacterData.LowerCase
    val lowerCaseRule = CharacterRule(lowerCaseChars)
    lowerCaseRule.setNumberOfCharacters(2)
    val upperCaseChars: CharacterData = EnglishCharacterData.UpperCase
    val upperCaseRule = CharacterRule(upperCaseChars)
    upperCaseRule.setNumberOfCharacters(2)
    val digitChars: CharacterData = EnglishCharacterData.Digit
    val digitRule = CharacterRule(digitChars)
    digitRule.setNumberOfCharacters(2)
    val specialChars: CharacterData = object : CharacterData {
        override fun getErrorCode(): String {
            return "error_gen_password"
        }

        override fun getCharacters(): String {
            return "!@#$%^&*()_+"
        }
    }
    val splCharRule = CharacterRule(specialChars)
    splCharRule.setNumberOfCharacters(2)
    return gen.generatePassword(
        8, splCharRule, lowerCaseRule,
        upperCaseRule, digitRule
    )
}

@ConfigurationProperties(prefix = "jwt")
@Component
class JWTProperties {
    object JWTExpirationTimeUnits {
        const val MILLISECOND = "millisecond"
        const val SECOND = "second"
        const val MINUTE = "minute"
        const val HOUR = "hour"
        const val DAY = "day"
        const val WEEK = "week"
    }

    var tokenExpiration: Long = 7

    var expirationUnit: String = JWTExpirationTimeUnits.WEEK

    var tokenSignKey = "111111"

    var jwtSubject = "BALANCE-USER"
}


@Slf4j
@Component
class JwtUtils : InitializingBean {

    @Autowired
    lateinit var jwtProperties: JWTProperties

    /**
     * export jwt properties to public static variables for global access
     */
    override fun afterPropertiesSet() {
        jwtSubject = jwtProperties.jwtSubject
        val expirationUnit: String = jwtProperties.expirationUnit
        val tokenExpirationValue: Long = jwtProperties.tokenExpiration
        when (expirationUnit) {
            SECOND -> tokenExpiration = tokenExpirationValue * 1000
            MINUTE -> tokenExpiration = tokenExpirationValue * 1000 * 60
            HOUR -> tokenExpiration = tokenExpirationValue * 1000 * 60 * 60
            DAY -> tokenExpiration = tokenExpirationValue * 1000 * 60 * 60 * 24
            WEEK -> tokenExpiration = tokenExpirationValue * 1000 * 60 * 60 * 24 * 7
            else -> tokenExpiration = tokenExpirationValue
        }
        tokenSignKey = jwtProperties.tokenSignKey
    }

    companion object {
        /**
         * token的过期时间, ms
         */
        var tokenExpiration: Long = 0

        /**
         * key to sign jwt payload
         */
        var tokenSignKey: String? = null

        /**
         * subject part of payload of jwt
         */
        var jwtSubject: String? = null

        // name of id properties in payload
        var TOKEN_PAYLOAD_ID_ATTR = "id"

        var TOKEN_PAYLOAD_TYPE_ATTR = "type"

        // random factor to make token payload more random
        const val TOKEN_PAYLOAD_RANDOM_FACTOR = "random-factor"

        /**
         * get user id in long type
         *
         * @return if user id is not in payload, and if user id can not be converted to a long , return null
         */
        fun getUserIdFromToken(claims: Claims): Long? {
            val userIdentity: String = claims.get(TOKEN_PAYLOAD_ID_ATTR, String::class.java)
                ?: return null
            return string2Long(userIdentity)
        }

        fun genToken(id: String?, entityType: EntityType): String {
            return genToken(id, entityType, tokenExpiration)
        }

        fun genToken(id: String?, entityType: EntityType, expireDuration: Long): String {
            val claim: MutableMap<String, Any?> = HashMap()
            claim[TOKEN_PAYLOAD_ID_ATTR] = id
            claim[TOKEN_PAYLOAD_TYPE_ATTR] = entityType.value
            claim[TOKEN_PAYLOAD_RANDOM_FACTOR] = UUID.randomUUID().toString()
            return createToken(claim, System.currentTimeMillis() + expireDuration)
        }

        /**
         * 生成JWT
         *
         * @param claimMap claim Map
         * @param expireAt 指定JWT的失效时间
         * @return JWT
         */
        fun createToken(claimMap: Map<String, Any?>, expireAt: Long): String {
            val expireAtDate = Date(expireAt)
            return createToken(claimMap, expireAtDate)
        }

        /**
         * 生成JWT，失效时间在配置文件中配置
         *
         * @param claimMap claim Map
         * @return JWT
         */
        fun createToken(claimMap: Map<String, Any?>): String {
            val expireAtDate = generateExpirationDate()
            return createToken(claimMap, expireAtDate)
        }

        private fun verifySignature(token: String?): Jws<Claims> {
            return verifySignature(tokenSignKey, token)
        }

        private fun verifySignature(signkey: String?, token: String?): Jws<Claims> {
            return Jwts.parser().setSigningKey(signkey).parseClaimsJws(token)
        }

        /**
         * 使用tokenSignKey验证JWT的签名，同时检测JWT是否过期
         *
         * @param token 目标JWT
         * @return 如果签名验证通过且JWT没有过期，返回JWT的claims，否则报错
         */
        fun verifyJWT(token: String?): Jws<Claims> {
            return verifySignature(token)
        }

        fun verifyJWT(signKey: String?, token: String?): Jws<Claims> {
            return verifySignature(signKey, token)
        }

        /**
         * 根据配置文件信息生成token的默认过期时间
         */
        private fun generateExpirationDate(): Date {
            return Date(System.currentTimeMillis() + tokenExpiration)
        }

        private fun createToken(claimMap: Map<String, Any?>, expireAtDate: Date): String {
            return Jwts.builder()
                .setSubject(jwtSubject)
                .setClaims(claimMap)
                .setExpiration(expireAtDate)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey) // .compressWith(CompressionCodecs.GZIP)
                .compact()
        }

        /**
         * 从token中获取指定的claim
         *
         * @param <T>       待获取的claim的类型
         * @param token     指定的token
         * @param claimName 待获取的claim的名字
         * @return 待获取的claim
        </T> */
        fun <T> getClaimFromJWT(token: String?, claimName: String?): T? {
            if (token == null || token == "") {
                return null
            }
            val claimsJws: Jws<Claims> = verifySignature(token)
            val claims: Claims = claimsJws.getBody()
            return claims.get(claimName) as T
        }

        private fun verifyJWTThrowBackendExceptions(tokenSignKey: String?, token: String?): Jws<Claims> {
            return try {
                verifyJWT(tokenSignKey, token)
            } catch (ex: ExpiredJwtException) {
                throw RuntimeException("Token Expired", ex)
            } catch (ex: Exception) {
                throw RuntimeException("Token Invalid", ex)
            }
        }

        fun verifyJWTThrowBackendExceptions(token: String?): Jws<Claims> {
            return verifyJWTThrowBackendExceptions(tokenSignKey, token)
        }
    }
}

fun string2Long(aString: String): Long? {
    try {
        return aString.toLong()
    } catch (e: NumberFormatException) {
        return null;
    }
}

enum class EntityType(var value: String) {
    WEB_USER("web-user"),
    APP("app"),
    UNKNOWN("unknown")
    ;

    companion object {
        fun fromValue(value: String?): EntityType {
            return when (value) {
                WEB_USER.value -> WEB_USER
                APP.value -> APP
                else -> UNKNOWN
            }
        }
    }

}
