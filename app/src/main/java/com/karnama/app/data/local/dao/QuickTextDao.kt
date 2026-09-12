package com.karnama.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.karnama.app.data.local.entity.QuickTextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickTextDao {

    @Query("SELECT * FROM quick_texts ORDER BY text ASC")
    fun observeAll(): Flow<List<QuickTextEntity>>

    @Insert
    suspend fun insert(quickText: QuickTextEntity): Long

    @Update
    suspend fun update(quickText: QuickTextEntity)

    @Delete
    suspend fun delete(quickText: QuickTextEntity)

    @Query("SELECT * FROM quick_texts")
    suspend fun getAllForBackup(): List<QuickTextEntity>

    @Insert
    suspend fun insertForBackup(quickText: QuickTextEntity)
}
