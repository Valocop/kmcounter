package com.km.counter

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.updateReturning

class CounterDao(database: Database) {

    object Counter : Table() {
        val name = text("name")
        val counter = long("value")

        override val primaryKey = PrimaryKey(name)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Counter)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(name: String, counter: Long) = dbQuery {
        Counter.insert {
            it[this.name] = name
            it[this.counter] = counter
        }
    }

    suspend fun find(name: String): Long? {
        return dbQuery {
            Counter
                .select(Counter.counter)
                .where(Counter.name eq name)
                .map { it[Counter.counter] }
                .singleOrNull()
        }
    }

    suspend fun increment(name: String): Long? {
        return dbQuery {
            Counter
                .updateReturning(
                    returning = listOf(Counter.counter),
                    where = { Counter.name eq name }
                ) { it.update(counter, counter + 1) }
                .singleOrNull()
                ?.get(Counter.counter)
        }
    }

    suspend fun findAll(): Map<String, Long> {
        return dbQuery {
            Counter
                .selectAll()
                .associate { it[Counter.name] to it[Counter.counter] }
        }
    }

    suspend fun delete(name: String) {
        dbQuery {
            Counter.deleteWhere { Counter.name eq name }
        }
    }
}