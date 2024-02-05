package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.*
import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.po.LedgerPO
import com.example.app.rest.CommonConstant.ENTITY_TOKEN
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.DAY
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.HOUR
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.MINUTE
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.SECOND
import com.example.app.rest.JWTProperties.JWTExpirationTimeUnits.WEEK
import com.example.app.rest.JwtUtils.Companion.genToken
import com.example.app.utils.DateTime
import com.example.app.utils.VarCaseConvertUtils
import io.jsonwebtoken.*
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.passay.CharacterData
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.NotBlank
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
        var credential: String? = null
    }

    @Autowired
    lateinit var userConfigMapper: UserConfigMapper

    @Autowired
    lateinit var userUserConfigMapper: UserUserConfigMapper

    @PostMapping("/user/auth")
    fun auth(@RequestBody userAuthRequest: UserAuthRequest): Any {
        var username = userAuthRequest.username!!;
        var password = userAuthRequest.credential!!;
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

        var configs = mutableListOf<UserConfigPO>();
        var tempEnList = userUserConfigMapper.selectList(QueryWrapper<UserUserConfigPO>().eq("user_id", exists.id))
        tempEnList.forEach {
            var tempEn = userConfigMapper.selectById(it.userConfigId)
            if (tempEn != null) {
                configs.add(tempEn)
            }
        }

        return object {
            var token = token;
            var configs = configs;
        }
    }

    class GenerateUserRequest {
        @NotEmpty
        var username: String? = null
    }

    @Autowired
    lateinit var recordTypeMapper: TransactionCategoryMapper

    @Autowired
    lateinit var userRecordTypeMapper: UserRecordTypeMapper

    @PostMapping("/user/generating")
    @Transactional
    fun generateUser(@RequestBody generateUserRequest: GenerateUserRequest): Any {
        var username = generateUserRequest.username!!;
        var exists = userDao.exists(QueryWrapper<UserPO>().eq("username", username))
        if (exists) {
            throw RuntimeException("user already exists")
        }

        var user = UserPO()
        user.username = username
        user.password = generatePassayPassword()
        userDao.insert(user)

        // create default ledger
        var userledgers = userLedgerMapper.getUserLedgers(user.id!!)
        var defaultLedger = userledgers.find { it.ledgerName == "default" }
        if (defaultLedger == null) {
            var newledger = LedgerPO(null, "default", DateTime.now().toString())
            ledgerMapper.insert(newledger)
            var userledger = UserLedgerPO(null, user.id, newledger.id)
            userLedgerMapper.insert(userledger)
        }

        var createUserConfig = { userId: Long, key: String, value: String ->
            var config = UserConfigPO(null, key, value)
            userConfigMapper.insert(config)
            var userUserConfig = UserUserConfigPO(null, userId, config.id)
            userUserConfigMapper.insert(userUserConfig)
        }

        createUserConfig(user.id!!, "default_ledger", "default")
        createUserConfig(user.id!!, "language", "en")

        var iconList = mapOf<String, String>(
            "daily" to "daily",
            "food_dish" to "food",
            "fruit" to "fruit",
            "drinks" to "drinks",
            "beer" to "alcohol",
            "transport" to "transportation",
            "entertainment" to "entertainment",
            "others" to "others",
            "water_cup" to "water",
            "icecream" to "snack",
            "mobile_phone" to "electronic",
            "medical_care" to "med",
            "tools_hardware" to "maintenance",
            "productivity" to "productivity",
            "furniture" to "furniture",
        )

        iconList.forEach {
            var recordType =
                recordTypeMapper.selectOne(QueryWrapper<TransactionCategoryPO>().eq("value", it.value))
            if (recordType == null) {
                throw RuntimeException("icon not found")
            }

            var newUserCat = UserRecordTypePO(null, user.id, recordType.id)
            userRecordTypeMapper.insert(newUserCat)
        }

        var token = genToken(user.id.toString(), EntityType.WEB_USER)
        return object {
            var token = token;
        }
    }

    class UserConfigRequest {
        @NotBlank
        var key: String? = null;

        @NotEmpty
        var value: String? = null;
    }

    class UpdateUserConfigRequest {
        @Valid
        var configs: List<UserConfigRequest> = mutableListOf()
    }

    @GetMapping("/user/configs")
    @Transactional
    @AuthLogin
    fun getUserConfigs(): Any {

        var user = getCurrentUser()

        val selectList = userUserConfigMapper.selectList(
            QueryWrapper<UserUserConfigPO>().eq(
                "user_id", user.id
            )
        )

        var userConfigs = selectList.map {
            userConfigMapper.selectById(it.userConfigId)
        }.toList()

        return userConfigs
    }

    @Autowired
    lateinit var ledgerMapper: LedgerMapper

    @PostMapping("/user/configs")
    @AuthLogin
    @Transactional
    fun updateUserConfig(@RequestBody @Valid updateUserConfigRequest: UpdateUserConfigRequest): Any {

        var user = requestCtx.get()["user"] as UserPO

        val configs1 = updateUserConfigRequest.configs

        val selectList = userUserConfigMapper.selectList(
            QueryWrapper<UserUserConfigPO>().eq(
                "user_id", user.id
            )
        )

        var userConfigs = selectList.map {
            userConfigMapper.selectById(it.userConfigId)
        }.toList()

        configs1.forEach {

            var userConfig = userConfigs.find { userConfig -> userConfig.key == it.key }
            if (userConfig == null) {
                userConfig = UserConfigPO()
                userConfig.key = it.key
                userConfig.value = it.value
                userConfigMapper.insert(userConfig)
            } else {
                userConfig.value = it.value
                userConfigMapper.updateById(userConfig)
            }

            var userUserConfig = userUserConfigMapper.selectOne(
                QueryWrapper<UserUserConfigPO>().eq(
                    "user_id", user.id
                ).eq(
                    "user_config_id", userConfig.id
                )
            )
            if (userUserConfig == null) {
                userUserConfig = UserUserConfigPO()
                userUserConfig.userConfigId = userConfig.id
                userUserConfig.userId = user.id
                userUserConfigMapper.insert(userUserConfig)
            }
        }

        val selectList1 = userUserConfigMapper.selectList(
            QueryWrapper<UserUserConfigPO>().eq(
                "user_id", user.id
            )
        )

        var userConfigs1 = selectList1.map {
            userConfigMapper.selectById(it.userConfigId)
        }.toList()

        return userConfigs1
    }

    @Autowired
    lateinit var userLedgerMapper: UserLedgerMapper

    @GetMapping("/user/info")
    @AuthLogin
    @Transactional
    fun getUserInfo(): Any {

        var user = requestCtx.get()["user"] as UserPO

        val selectList = userUserConfigMapper.selectList(
            QueryWrapper<UserUserConfigPO>().eq(
                "user_id", user.id
            )
        )

        var userConfigs = selectList.map {
            userConfigMapper.selectById(it.userConfigId)
        }.filterNotNull().toList()


        return object {
            var configs = userConfigs
        }
    }

}


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.RUNTIME
)
@Component
annotation class AuthLogin()


object CommonConstant {
    const val ENTITY_TOKEN = "entity-token"
}

val requestCtx = ThreadLocal<MutableMap<String, Any>>()

fun getCurrentUser(): UserPO {
    try {
        return requestCtx.get()["user"] as UserPO
    } catch (ex: Throwable) {
        throw RuntimeException("user auth failed")
    }
}

@Component
class AuthInterceptor : HandlerInterceptor {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var userMapper: UserMapper;

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        if (handler !is HandlerMethod) {
            return true
        }

        log.info("get into AuthenticationInterceptor")

        val method = handler.method
        method.getAnnotation(AuthLogin::class.java) ?: return true

        // ensure request is attached with token
        val token = getTokenFromRequestQueryOrHeader(request)
        token ?: throw RuntimeException("token missing")

        requestCtx.set(mutableMapOf())

        // validate jwt expiration and format
        val claimsJws = JwtUtils.verifyJWTThrowBackendExceptions(token)

        // validate user exists
        val userId = JwtUtils.getUserIdFromToken(claimsJws.body) ?: throw RuntimeException("TOKEN_INVALID")
        val userpo = userMapper.selectById(userId) ?: throw RuntimeException("TOKEN_INVALID")

        val requestCtxMap = requestCtx.get()
        requestCtxMap["user"] = userpo

        return true;
    }

    private fun getTokenFromRequestQueryOrHeader(request: HttpServletRequest): String? {

        // this code is used to be compatible with poor design of multiple entity token authentication
        var entityTokenInQuery = request.getParameter(VarCaseConvertUtils.lowerHyphen2LowerCamel(ENTITY_TOKEN))
        var entityTokenInHeader = request.getHeader(VarCaseConvertUtils.lowerHyphen2LowerCamel(ENTITY_TOKEN))

        // these tryings are for compatibility of old design
        entityTokenInQuery ?: run { entityTokenInQuery = request.getParameter(ENTITY_TOKEN) }
        entityTokenInHeader ?: run { entityTokenInQuery = request.getHeader(ENTITY_TOKEN) }
        return entityTokenInQuery ?: entityTokenInHeader
    }

}


@Configuration
class InterceptorConfig : WebMvcConfigurer {

    @Autowired
    var applicationContext: ApplicationContext? = null

    override fun addInterceptors(registry: InterceptorRegistry) {
        for (interceptorClass in youSeeAllInterceptors) {
            registry.addInterceptor(applicationContext!!.getBean(interceptorClass))
                .addPathPatterns("/**")
        }
    }

    companion object {
        private val youSeeAllInterceptors = listOf<Class<out HandlerInterceptor>>(
            AuthInterceptor::class.java,
        )
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
