package com.example.app.config

import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.po.LedgerPO
import com.example.app.utils.DateTime
import com.example.app.utils.DateUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.ErrorPageRegistrar
import org.springframework.boot.web.server.ErrorPageRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.*
import javax.annotation.PostConstruct

@Component
class Test :CommandLineRunner{
    @Autowired
    lateinit var ledgerMapper: LedgerMapper
    override fun run(vararg args: String?) {
        val selectByMap = ledgerMapper.selectByMap(mapOf("name" to "default"))
        if (selectByMap.isEmpty()) {
            ledgerMapper.insert(LedgerPO(null, "default", DateTime.now().toString()))
        } else if (selectByMap.size > 1) {
            throw RuntimeException("ledger name default should be only one")
        }
    }


}


@Configuration
class RequestCorsFilter {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOriginPattern("*")
        config.allowedHeaders = Arrays.asList("Origin", "Content-Type", "Accept", "responseType", "Authorization", "entity-token")
        config.allowedMethods = Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}

@Component
class StaticServerErrorPageConfig : ErrorPageRegistrar {
    override fun registerErrorPages(registry: ErrorPageRegistry) {
        val error404Page = ErrorPage(HttpStatus.NOT_FOUND, "/index.html")
        registry.addErrorPages(error404Page)
    }
}
