package com.meridian.androidai.core.memory

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class AndroidMemoryStore(
    context: Context
) : MemoryStore, AutoCloseable {

    private val databaseHelper = MemoryDatabaseHelper(context)

    override fun save(record: MemoryRecord): MemoryRecord {
        val values = ContentValues().apply {
            put(MemoryDatabaseHelper.COLUMN_ID, record.id)
            put(MemoryDatabaseHelper.COLUMN_TYPE, record.type.name)
            put(MemoryDatabaseHelper.COLUMN_CONTENT, record.content)
            put(MemoryDatabaseHelper.COLUMN_SOURCE, record.source)
            put(MemoryDatabaseHelper.COLUMN_TIMESTAMP, record.timestamp.toEpochMilli())
            put(MemoryDatabaseHelper.COLUMN_IMPORTANCE, record.importance)
            put(MemoryDatabaseHelper.COLUMN_CONFIDENCE, record.confidence)
            put(MemoryDatabaseHelper.COLUMN_CONTEXT, encodeContext(record.context))
            put(MemoryDatabaseHelper.COLUMN_RELATIONSHIPS, encodeRelationships(record.relationships))
            record.lastUsed?.let { put(MemoryDatabaseHelper.COLUMN_LAST_USED, it.toEpochMilli()) }
                ?: putNull(MemoryDatabaseHelper.COLUMN_LAST_USED)
            put(MemoryDatabaseHelper.COLUMN_STATUS, record.status.name)
        }
        databaseHelper.writableDatabase.insertWithOnConflict(
            MemoryDatabaseHelper.TABLE_MEMORIES, null, values, SQLiteDatabase.CONFLICT_REPLACE
        )
        return record
    }

    override fun get(id: String): MemoryRecord? = databaseHelper.readableDatabase.query(
        MemoryDatabaseHelper.TABLE_MEMORIES, ALL_COLUMNS,
        "${MemoryDatabaseHelper.COLUMN_ID} = ?", arrayOf(id), null, null, null, "1"
    ).use { cursor -> if (cursor.moveToFirst()) cursor.toMemoryRecordOrNull() else null }

    override fun query(query: MemoryQuery): List<MemoryRecord> {
        val selections = mutableListOf<String>()
        val arguments = mutableListOf<String>()
        if (query.statuses.isEmpty()) selections += "1 = 0"
        else {
            selections += "${MemoryDatabaseHelper.COLUMN_STATUS} IN (${query.statuses.joinToString(",") { "?" }})"
            arguments += query.statuses.map { it.name }
        }
        if (query.types.isNotEmpty()) {
            selections += "${MemoryDatabaseHelper.COLUMN_TYPE} IN (${query.types.joinToString(",") { "?" }})"
            arguments += query.types.map { it.name }
        }
        query.text?.trim()?.takeIf { it.isNotEmpty() }?.let {
            selections += "(${MemoryDatabaseHelper.COLUMN_CONTENT} LIKE ? OR ${MemoryDatabaseHelper.COLUMN_CONTEXT} LIKE ?)"
            val pattern = "%$it%"
            arguments += pattern
            arguments += pattern
        }
        return databaseHelper.readableDatabase.query(
            MemoryDatabaseHelper.TABLE_MEMORIES, ALL_COLUMNS,
            selections.joinToString(" AND "), arguments.toTypedArray(), null, null,
            "${MemoryDatabaseHelper.COLUMN_IMPORTANCE} DESC, ${MemoryDatabaseHelper.COLUMN_TIMESTAMP} DESC",
            query.limit.toString()
        ).use { cursor ->
            buildList { while (cursor.moveToNext()) cursor.toMemoryRecordOrNull()?.let(::add) }
        }
    }

    override fun delete(id: String): Boolean = databaseHelper.writableDatabase.delete(
        MemoryDatabaseHelper.TABLE_MEMORIES, "${MemoryDatabaseHelper.COLUMN_ID} = ?", arrayOf(id)
    ) > 0

    override fun close() = databaseHelper.close()

    private fun Cursor.toMemoryRecordOrNull(): MemoryRecord? = runCatching {
        val lastUsedIndex = getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_LAST_USED)
        MemoryRecord(
            id = getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_ID)),
            type = MemoryType.valueOf(getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_TYPE))),
            content = getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_CONTENT)),
            source = getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_SOURCE)),
            timestamp = Instant.ofEpochMilli(getLong(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_TIMESTAMP))),
            importance = getDouble(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_IMPORTANCE)),
            confidence = getDouble(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_CONFIDENCE)),
            context = decodeContext(getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_CONTEXT))),
            relationships = decodeRelationships(getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_RELATIONSHIPS))),
            lastUsed = if (isNull(lastUsedIndex)) null else Instant.ofEpochMilli(getLong(lastUsedIndex)),
            status = MemoryStatus.valueOf(getString(getColumnIndexOrThrow(MemoryDatabaseHelper.COLUMN_STATUS)))
        )
    }.getOrNull()

    private fun encodeContext(context: Map<String, String>): String = JSONObject().apply {
        context.forEach { (key, value) -> put(key, value) }
    }.toString()

    private fun decodeContext(value: String): Map<String, String> {
        val json = JSONObject(value)
        return json.keys().asSequence().associateWith { json.getString(it) }
    }

    private fun encodeRelationships(relationships: Set<String>): String = JSONArray().apply {
        relationships.forEach(::put)
    }.toString()

    private fun decodeRelationships(value: String): Set<String> {
        val json = JSONArray(value)
        return buildSet(json.length()) { for (index in 0 until json.length()) add(json.getString(index)) }
    }

    companion object {
        private val ALL_COLUMNS = arrayOf(
            MemoryDatabaseHelper.COLUMN_ID, MemoryDatabaseHelper.COLUMN_TYPE,
            MemoryDatabaseHelper.COLUMN_CONTENT, MemoryDatabaseHelper.COLUMN_SOURCE,
            MemoryDatabaseHelper.COLUMN_TIMESTAMP, MemoryDatabaseHelper.COLUMN_IMPORTANCE,
            MemoryDatabaseHelper.COLUMN_CONFIDENCE, MemoryDatabaseHelper.COLUMN_CONTEXT,
            MemoryDatabaseHelper.COLUMN_RELATIONSHIPS, MemoryDatabaseHelper.COLUMN_LAST_USED,
            MemoryDatabaseHelper.COLUMN_STATUS
        )
    }
}
