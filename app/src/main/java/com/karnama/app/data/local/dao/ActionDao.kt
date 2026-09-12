package com.karnama.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.karnama.app.data.local.entity.ActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions WHERE taskId = :taskId ORDER BY createdAt DESC")
    fun observeActionsForTask(taskId: Long): Flow<List<ActionEntity>>

    @Query("SELECT taskId FROM actions GROUP BY taskId")
    fun observeTaskIdsWithActions(): Flow<List<Long>>

    @Insert
    suspend fun insert(action: ActionEntity): Long

    @Delete
    suspend fun delete(action: ActionEntity)

    @Query("SELECT COUNT(*) FROM actions WHERE taskId = :taskId")
    suspend fun countForTask(taskId: Long): Int

    @Query("SELECT * FROM actions")
    suspend fun getAllForBackup(): List<ActionEntity>

    @Insert
    suspend fun insertForBackup(action: ActionEntity)
}
