package com.karnama.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.karnama.app.data.local.dao.ActionDao
import com.karnama.app.data.local.dao.QuickTextDao
import com.karnama.app.data.local.dao.TaskDao
import com.karnama.app.data.local.entity.ActionEntity
import com.karnama.app.data.local.entity.QuickTextEntity
import com.karnama.app.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, ActionEntity::class, QuickTextEntity::class],
    version = 1,
    exportSchema = true
)
abstract class KarnamaDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun actionDao(): ActionDao
    abstract fun quickTextDao(): QuickTextDao

    /** پاک کردن کامل جدول‌ها پیش از بازیابی از بک‌آپ */
    fun clearAllTablesForBackup() = clearAllTables()

    companion object {
        @Volatile
        private var INSTANCE: KarnamaDatabase? = null

        fun getInstance(context: Context): KarnamaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KarnamaDatabase::class.java,
                    "karnama.db"
                ).addMigrations(*ALL_MIGRATIONS)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
