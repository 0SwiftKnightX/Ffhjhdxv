package com.meridian.androidai.core.memory

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class MemoryDatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_MEMORIES (
                $COLUMN_ID TEXT PRIMARY KEY NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_SOURCE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_IMPORTANCE REAL NOT NULL,
                $COLUMN_CONFIDENCE REAL NOT NULL,
                $COLUMN_CONTEXT TEXT NOT NULL,
                $COLUMN_RELATIONSHIPS TEXT NOT NULL,
                $COLUMN_LAST_USED INTEGER,
                $COLUMN_STATUS TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_memories_status_type ON $TABLE_MEMORIES ($COLUMN_STATUS, $COLUMN_TYPE)")
        db.execSQL("CREATE INDEX idx_memories_importance_timestamp ON $TABLE_MEMORIES ($COLUMN_IMPORTANCE DESC, $COLUMN_TIMESTAMP DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMORIES")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "meridian_memory.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_MEMORIES = "memories"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_SOURCE = "source"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IMPORTANCE = "importance"
        const val COLUMN_CONFIDENCE = "confidence"
        const val COLUMN_CONTEXT = "context"
        const val COLUMN_RELATIONSHIPS = "relationships"
        const val COLUMN_LAST_USED = "last_used"
        const val COLUMN_STATUS = "status"
    }
}
