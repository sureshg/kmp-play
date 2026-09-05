package dev.suresh.http

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Timeout(
    val connection: Duration = 5.seconds,
    val read: Duration = 5.seconds,
    val write: Duration = 5.seconds,
)

@Serializable
data class Retry(
    val attempts: Int = 2,
    val maxDelay: Duration = 5.seconds,
)

@Serializable
data class ErrorStatus(val code: Int, val message: String, val details: String? = null)
