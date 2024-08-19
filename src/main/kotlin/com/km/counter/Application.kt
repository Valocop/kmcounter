package com.km.counter

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
    }
    install(ContentNegotiation) {
        json()
    }

    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    val counterDao = CounterDao(database)

    routing {
        // Create
        post("/Create") {
            val (name, counter) = call.receive<CounterRequest>()

            counterDao.create(name, counter)

            call.respond(HttpStatusCode.OK)
        }

        // Read
        get("/Get/{counter}") {
            val name = call.parameters["counter"] ?: throw IllegalArgumentException("Invalid counter")

            val counter = counterDao.find(name) ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, counter)
        }

        // Delete
        post("/Delete/{counter}") {
            val name = call.parameters["counter"] ?: throw IllegalArgumentException("Invalid counter")

            counterDao.delete(name)

            call.respond(HttpStatusCode.OK)
        }

        // Increment
        post("/Increment/{counter}") {
            val name = call.parameters["counter"] ?: throw IllegalArgumentException("Invalid counter")

            val counter = counterDao.increment(name) ?: return@post call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, counter)
        }

        // GetAll
        get("/GetAll") {
            val result = counterDao.findAll()

            call.respond(HttpStatusCode.OK, result)
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(factory = Netty, port = 88, module = Application::module).start(wait = true)
}