package com.meridian.androidai.core.knowledge

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class AndroidKnowledgeStore(private val helper: KnowledgeDatabaseHelper) : KnowledgeStore, AutoCloseable {
    private val database: SQLiteDatabase get() = helper.writableDatabase

    override fun save(record: KnowledgeRecord): KnowledgeRecord {
        val values = ContentValues().apply {
            put(KnowledgeDatabaseHelper.COLUMN_ID, record.id)
            put(KnowledgeDatabaseHelper.COLUMN_TYPE, record.type.name)
            put(KnowledgeDatabaseHelper.COLUMN_TITLE, record.title)
            put(KnowledgeDatabaseHelper.COLUMN_CONTENT, record.content)
            put(KnowledgeDatabaseHelper.COLUMN_SOURCE, record.source)
            put(KnowledgeDatabaseHelper.COLUMN_TIMESTAMP, record.timestamp.toEpochMilli())
            put(KnowledgeDatabaseHelper.COLUMN_CONFIDENCE, record.confidence)
            put(KnowledgeDatabaseHelper.COLUMN_TAGS, JSONArray(record.tags.toList()).toString())
            put(KnowledgeDatabaseHelper.COLUMN_METADATA, JSONObject(record.metadata).toString())
            put(KnowledgeDatabaseHelper.COLUMN_STATUS, record.status.name)
        }
        database.insertWithOnConflict(KnowledgeDatabaseHelper.TABLE_KNOWLEDGE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return record
    }

    override fun get(id: String): KnowledgeRecord? =
        database.query(KnowledgeDatabaseHelper.TABLE_KNOWLEDGE, null,
            "${KnowledgeDatabaseHelper.COLUMN_ID} = ?", arrayOf(id), null, null, null).use { cursor ->
            if (cursor.moveToFirst()) cursor.toRecordOrNull() else null
        }

    override fun query(query: KnowledgeQuery): List<KnowledgeRecord> {
        val where = mutableListOf<String>()
        val args = mutableListOf<String>()
        if (query.statuses.isNotEmpty()) {
            where += "${KnowledgeDatabaseHelper.COLUMN_STATUS} IN (${query.statuses.joinToString(",") { "?" }})"
            args += query.statuses.map { it.name }
        }
        if (query.types.isNotEmpty()) {
            where += "${KnowledgeDatabaseHelper.COLUMN_TYPE} IN (${query.types.joinToString(",") { "?" }})"
            args += query.types.map { it.name }
        }
        query.text?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }?.let { text ->
            val pattern = "%$text%"
            where += "(LOWER(${KnowledgeDatabaseHelper.COLUMN_TITLE}) LIKE ? OR LOWER(${KnowledgeDatabaseHelper.COLUMN_CONTENT}) LIKE ? OR LOWER(${KnowledgeDatabaseHelper.COLUMN_SOURCE}) LIKE ? OR LOWER(${KnowledgeDatabaseHelper.COLUMN_TAGS}) LIKE ?)"
            repeat(4) { args += pattern }
        }
        val selection = where.takeIf { it.isNotEmpty() }?.joinToString(" AND ")
        val order = "${KnowledgeDatabaseHelper.COLUMN_CONFIDENCE} DESC, ${KnowledgeDatabaseHelper.COLUMN_TIMESTAMP} DESC"
        database.query(KnowledgeDatabaseHelper.TABLE_KNOWLEDGE, null, selection, args.toTypedArray(), null, null, order, query.limit.toString()).use { cursor ->
            val requestedTags = query.tags.map { it.lowercase() }.toSet()
            val records = mutableListOf<KnowledgeRecord>()
            while (cursor.moveToNext()) {
                cursor.toRecordOrNull()?.takeIf { record ->
                    requestedTags.isEmpty() || requestedTags.all { tag -> record.tags.any { it.lowercase() == tag } }
                }?.let(records::add)
            }
            return records
        }
    }

    override fun delete(id: String): Boolean =
        database.delete(KnowledgeDatabaseHelper.TABLE_KNOWLEDGE, "${KnowledgeDatabaseHelper.COLUMN_ID} = ?", arrayOf(id)) > 0

    override fun close() = helper.close()

    private fun android.database.Cursor.toRecordOrNull(): KnowledgeRecord? = runCatching {
        val tagsArray = JSONArray(getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_TAGS)))
        val tags = buildSet { for (i in 0 until tagsArray.length()) add(tagsArray.getString(i)) }
        val metadataObject = JSONObject(getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_METADATA)))
        val metadata = buildMap {
            val keys = metadataObject.keys()
            while (keys.hasNext()) { val key = keys.next(); put(key, metadataObject.getString(key)) }
        }
        KnowledgeRecord(
            id = getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_ID)),
            type = KnowledgeType.valueOf(getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_TYPE))),
            title = getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_TITLE)),
            content = getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_CONTENT)),
            source = getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_SOURCE)),
            timestamp = Instant.ofEpochMilli(getLong(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_TIMESTAMP))),
            confidence = getDouble(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_CONFIDENCE)),
            tags = tags, metadata = metadata,
            status = KnowledgeStatus.valueOf(getString(getColumnIndexOrThrow(KnowledgeDatabaseHelper.COLUMN_STATUS)))
        )
    }.getOrNull()
}
