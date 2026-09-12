package com.karnama.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.karnama.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE dueDateEpochDay = :epochDay ORDER BY completed ASC, orderIndex ASC, id ASC")
    fun observeTasksForDate(epochDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY updatedAt DESC")
    fun observeCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("UPDATE tasks SET completed = :completed, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET dueDateEpochDay = :newEpochDay, updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun moveTasksToDate(ids: List<Long>, newEpochDay: Long, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDateEpochDay = :epochDay")
    suspend fun countForDate(epochDay: Long): Int

    @Query("SELECT COALESCE(MAX(orderIndex), 0) FROM tasks WHERE dueDateEpochDay = :epochDay")
    suspend fun maxOrderIndexForDate(epochDay: Long): Long

    @Query("SELECT * FROM tasks")
    suspend fun getAllForBackup(): List<TaskEntity>

    @Insert
    suspend fun insertForBackup(task: TaskEntity)
}
