package com.meridian.androidai.core.knowledge

import java.util.concurrent.ConcurrentHashMap

class InMemoryKnowledgeStore : KnowledgeStore {
    private val records = ConcurrentHashMap<String, KnowledgeRecord>()

    override fun save(record: KnowledgeRecord): KnowledgeRecord {
        records[record.id] = record
        return record
    }

    override fun get(id: String): KnowledgeRecord? = records[id]

    override fun query(query: KnowledgeQuery): List<KnowledgeRecord> {
        val normalized = query.text?.trim()?.lowercase()
        val normalizedTags = query.tags.map { it.lowercase() }.toSet()

        return records.values
            .asSequence()
            .filter { it.status in query.statuses }
            .filter { query.types.isEmpty() || it.type in query.types }
            .filter { normalizedTags.isEmpty() || normalizedTags.all { tag -> it.tags.any { value -> value.lowercase() == tag } } }
            .filter {
                normalized.isNullOrEmpty() ||
                    it.title.lowercase().contains(normalized) ||
                    it.content.lowercase().contains(normalized) ||
                    it.source.lowercase().contains(normalized) ||
                    it.tags.any { tag -> tag.lowercase().contains(normalized) }
            }
            .sortedWith(
                compareByDescending<KnowledgeRecord> { it.confidence }
                    .thenByDescending { it.timestamp }
            )
            .take(query.limit)
            .toList()
    }

    override fun delete(id: String): Boolean = records.remove(id) != null
}
