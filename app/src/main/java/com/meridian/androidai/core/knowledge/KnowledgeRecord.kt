package com.meridian.androidai.core.knowledge

import java.time.Instant
import java.util.UUID

data class KnowledgeRecord(
    val id: String = UUID.randomUUID().toString(),
    val type: KnowledgeType,
    val title: String,
    val content: String,
    val source: String,
    val timestamp: Instant = Instant.now(),
    val confidence: Double = 1.0,
    val tags: Set<String> = emptySet(),
    val metadata: Map<String, String> = emptyMap(),
    val status: KnowledgeStatus = KnowledgeStatus.ACTIVE
) {
    init {
        require(title.isNotBlank()) { "title must not be blank" }
        require(content.isNotBlank()) { "content must not be blank" }
        require(source.isNotBlank()) { "source must not be blank" }
        require(confidence in 0.0..1.0) { "confidence must be between 0.0 and 1.0" }
    }
}
