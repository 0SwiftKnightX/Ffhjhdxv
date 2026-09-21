package com.meridian.androidai.core.memory

import java.time.Instant
import java.util.UUID

data class MemoryRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: MemoryType,
    val content: String,
    val source: String,
    val timestamp: Instant = Instant.now(),
    val importance: Double = 0.5,
    val confidence: Double = 1.0,
    val context: Map<String, String> = emptyMap(),
    val relationships: Set<String> = emptySet(),
    val lastUsed: Instant? = null,
    val status: MemoryStatus = MemoryStatus.ACTIVE
) {
    init {
        require(content.isNotBlank()) { "content must not be blank" }
        require(source.isNotBlank()) { "source must not be blank" }
        require(importance in 0.0..1.0) { "importance must be between 0.0 and 1.0" }
        require(confidence in 0.0..1.0) { "confidence must be between 0.0 and 1.0" }
    }
}
