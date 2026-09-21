package com.meridian.androidai.core.memory

interface MemoryStore {
    fun save(record: MemoryRecord): MemoryRecord
    fun get(id: String): MemoryRecord?
    fun query(query: MemoryQuery): List<MemoryRecord>
    fun delete(id: String): Boolean
}
