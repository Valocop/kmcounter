package com.km.counter

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class CounterDaoTest : FreeSpec() {
    companion object {
        private val containerDb = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:15"))
            .apply {
                withDatabaseName("testdb")
                withUsername("root")
                withPassword("root")
                start()
            }

        private val database = Database.connect(
            url = containerDb.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = containerDb.username,
            password = containerDb.password
        )
    }

    init {
        "should create a new counter" {
            val counterDao = CounterDao(database)

            counterDao.create("name", 1)

            counterDao.find("name") shouldBe 1L
        }

        "should delete a counter" {
            val counterDao = CounterDao(database)

            counterDao.create("name", 1)

            counterDao.delete("name")

            counterDao.find("name") shouldBe null
        }

        "should concurrent increment counter" {
            val counterDao = CounterDao(database)

            counterDao.create("name", 0)

            val coroutineScope = CoroutineScope(Dispatchers.Default)

            // 1_000 concurrent increments
            List(1000) {
                coroutineScope.async {
                    counterDao.increment("name")
                }
            }
                .awaitAll()

            counterDao.find("name") shouldBe 1000L
        }

        "should get all counters" {
            val counterDao = CounterDao(database)

            counterDao.create("counter1", 1)
            counterDao.create("counter2", 1)

            counterDao.findAll() shouldHaveSize 2
        }

        afterTest {
            transaction {
                SchemaUtils.drop(CounterDao.Counter)
            }
        }
    }
}
