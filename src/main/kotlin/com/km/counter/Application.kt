package com.km.counter

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.callloging.CallLogging
import org.jetbrains.exposed.sql.Table

// Define the table
object Counters : Table(name = "counter") {
    val name = text("name")
    val value = integer("value")
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
    }
    install(ContentNegotiation) {
        json()
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}