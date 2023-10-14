package com.example.app.config

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


class AppConfig {
}

// @Configuration
// class CorsConfig : WebMvcConfigurer {
//     override fun addCorsMappings(registry: CorsRegistry) {
//         registry.addMapping("/**")
//             .allowedOrigins("*")
//             .allowedMethods("GET", "POST", "PUT", "DELETE")
//             .allowedHeaders("*")
//             .allowCredentials(true)
//     }
// }

@Configuration
class RequestCorsFilter {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.setAllowedOriginPatterns(
            listOf(
                "http://localhost:[*]",
                "https://*.wenchao.fit:[*]",

                "http://127.0.0.1:[*]",
                "https://127.0.0.1:[*]",
                "https://editor.swagger.io",

                "http://[*]:[*]",
                "https://[*]:[*]",

                // for debug
                "https://49.232.155.160:[*]",
                "http://49.232.155.160:[*]",
            )
        );
        config.allowedHeaders = Arrays.asList("Origin", "Content-Type", "Accept", "responseType", "Authorization")
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
