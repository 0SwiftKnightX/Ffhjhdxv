package com.meridian.androidai.core.knowledge

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class KnowledgeDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $TABLE_KNOWLEDGE (
            $COLUMN_ID TEXT PRIMARY KEY NOT NULL, $COLUMN_TYPE TEXT NOT NULL,
            $COLUMN_TITLE TEXT NOT NULL, $COLUMN_CONTENT TEXT NOT NULL,
            $COLUMN_SOURCE TEXT NOT NULL, $COLUMN_TIMESTAMP INTEGER NOT NULL,
            $COLUMN_CONFIDENCE REAL NOT NULL, $COLUMN_TAGS TEXT NOT NULL,
            $COLUMN_METADATA TEXT NOT NULL, $COLUMN_STATUS TEXT NOT NULL)""")
        db.execSQL("CREATE INDEX idx_knowledge_status_type ON $TABLE_KNOWLEDGE ($COLUMN_STATUS, $COLUMN_TYPE)")
        db.execSQL("CREATE INDEX idx_knowledge_confidence_timestamp ON $TABLE_KNOWLEDGE ($COLUMN_CONFIDENCE, $COLUMN_TIMESTAMP)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KNOWLEDGE")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "meridian_knowledge.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_KNOWLEDGE = "knowledge"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_SOURCE = "source"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_CONFIDENCE = "confidence"
        const val COLUMN_TAGS = "tags"
        const val COLUMN_METADATA = "metadata"
        const val COLUMN_STATUS = "status"
    }
}
