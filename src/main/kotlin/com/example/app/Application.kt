package com.example.app

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.UUID

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    println(UUID.randomUUID().toString())
    println(UUID.randomUUID().toString())
    println(UUID.randomUUID().toString())
    println(UUID.randomUUID().toString())
    runApplication<Application>(*args)
}

@Component
class Runner : CommandLineRunner {
    override fun run(vararg args: String?) {

    }

}