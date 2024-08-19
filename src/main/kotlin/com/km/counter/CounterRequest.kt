package com.km.counter

import kotlinx.serialization.Serializable

@Serializable
data class CounterRequest(
    val name: String,
    val counter: Long
)
