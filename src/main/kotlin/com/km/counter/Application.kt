package com.km.counter

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
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
        url = "jdbc:postgresql://localhost:55432/counter",
        user = "root",
        password = "root",
        driver = "org.postgresql.Driver"
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
        get("/Get") {
            val name = call.parameters["counter"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing 'counter' query parameter"
            )

            val counter = counterDao.find(name) ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, counter)
        }

        // Delete
        post("/Delete") {
            val name = call.parameters["counter"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Missing 'counter' query parameter"
            )

            counterDao.delete(name)

            call.respond(HttpStatusCode.OK)
        }

        // Increment
        post("/Increment") {
            val name = call.parameters["counter"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Missing 'counter' query parameter"
            )

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