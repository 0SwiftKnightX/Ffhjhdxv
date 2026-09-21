package com.meridian.androidai.core.memory

import java.util.concurrent.ConcurrentHashMap

class InMemoryStore : MemoryStore {
    private val records = ConcurrentHashMap<String, MemoryRecord>()

    override fun save(record: MemoryRecord): MemoryRecord {
        records[record.id] = record
        return record
    }

    override fun get(id: String): MemoryRecord? = records[id]

    override fun query(query: MemoryQuery): List<MemoryRecord> {
        val normalized = query.text?.trim()?.lowercase()

        return records.values
            .asSequence()
            .filter { it.status in query.statuses }
            .filter { query.types.isEmpty() || it.type in query.types }
            .filter {
                normalized.isNullOrEmpty() ||
                    it.content.lowercase().contains(normalized) ||
                    it.context.values.any { value -> value.lowercase().contains(normalized) }
            }
            .sortedWith(
                compareByDescending<MemoryRecord> { it.importance }
                    .thenByDescending { it.timestamp }
            )
            .take(query.limit)
            .toList()
    }

    override fun delete(id: String): Boolean = records.remove(id) != null
}
