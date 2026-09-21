package com.meridian.androidai.core.knowledge

data class KnowledgeQuery(
    val text: String? = null,
    val types: Set<KnowledgeType> = emptySet(),
    val statuses: Set<KnowledgeStatus> = setOf(KnowledgeStatus.ACTIVE),
    val tags: Set<String> = emptySet(),
    val limit: Int = 20
) {
    init {
        require(limit > 0) { "limit must be greater than zero" }
    }
}
