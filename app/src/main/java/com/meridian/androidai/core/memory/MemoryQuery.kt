package com.meridian.androidai.core.memory

data class MemoryQuery(
    val text: String? = null,
    val types: Set<MemoryType> = emptySet(),
    val statuses: Set<MemoryStatus> = setOf(MemoryStatus.ACTIVE),
    val limit: Int = 20
) {
    init {
        require(limit > 0) { "limit must be greater than zero" }
    }
}
