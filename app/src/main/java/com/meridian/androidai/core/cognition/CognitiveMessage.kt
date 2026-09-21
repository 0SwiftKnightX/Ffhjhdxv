package com.meridian.androidai.core.cognition

import java.time.Instant
import java.util.UUID

data class CognitiveMessage(
    val source: CoreId,
    val destination: CoreId,
    val type: MessageType,
    val payload: Map<String, String> = emptyMap(),
    val context: Map<String, String> = emptyMap(),
    val priority: MessagePriority = MessagePriority.NORMAL,
    val confidence: Double = 1.0,
    val timestamp: Instant = Instant.now(),
    val correlationId: String = UUID.randomUUID().toString(),
    val taskId: String? = null,
    val authorityRequired: Boolean = false,
    val timeoutMillis: Long? = null
) {
    init {
        require(confidence in 0.0..1.0) { "confidence must be between 0.0 and 1.0" }
        require(timeoutMillis == null || timeoutMillis >= 0) { "timeoutMillis must be non-negative" }
    }
}
