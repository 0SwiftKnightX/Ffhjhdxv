package com.meridian.androidai.core.knowledge

interface KnowledgeStore {
    fun save(record: KnowledgeRecord): KnowledgeRecord
    fun get(id: String): KnowledgeRecord?
    fun query(query: KnowledgeQuery): List<KnowledgeRecord>
    fun delete(id: String): Boolean
}
