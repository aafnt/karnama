package com.karnama.app.data.repository

import com.karnama.app.data.local.dao.ActionDao
import com.karnama.app.data.local.dao.TaskDao
import com.karnama.app.data.local.entity.ActionEntity
import com.karnama.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    private val actionDao: ActionDao
) {

    /** جریان کارهای یک روز خاص، همراه با پرچم «آیا اقدام دارد» برای هر کار */
    fun observeTasksForDate(epochDay: Long): Flow<List<Task>> {
        return combine(
            taskDao.observeTasksForDate(epochDay),
            actionDao.observeTaskIdsWithActions()
        ) { tasks, idsWithActions ->
            val idsWithActionsSet = idsWithActions.toHashSet()
            tasks.map { it.toDomain(hasActions = idsWithActionsSet.contains(it.id)) }
        }
    }

    fun observeCompletedTasks(): Flow<List<Task>> {
        return combine(
            taskDao.observeCompletedTasks(),
            actionDao.observeTaskIdsWithActions()
        ) { tasks, idsWithActions ->
            val idsWithActionsSet = idsWithActions.toHashSet()
            tasks.map { it.toDomain(hasActions = idsWithActionsSet.contains(it.id)) }
        }
    }

    fun observeActionsForTask(taskId: Long): Flow<List<TaskAction>> {
        return actionDao.observeActionsForTask(taskId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun addTask(title: String, epochDay: Long, description: String? = null): Long {
        val nextOrder = taskDao.maxOrderIndexForDate(epochDay) + 1
        return taskDao.insert(
            TaskEntity(
                title = title.trim(),
                description = description,
                dueDateEpochDay = epochDay,
                orderIndex = nextOrder
            )
        )
    }

    suspend fun setCompleted(id: Long, completed: Boolean) = taskDao.setCompleted(id, completed)

    suspend fun updateTitle(id: Long, newTitle: String) = taskDao.updateTitle(id, newTitle.trim())

    suspend fun moveTasks(ids: List<Long>, newEpochDay: Long) = taskDao.moveTasksToDate(ids, newEpochDay)

    suspend fun deleteTasks(ids: List<Long>) = taskDao.deleteByIds(ids)

    suspend fun addAction(taskId: Long, text: String): Long {
        return actionDao.insert(ActionEntity(taskId = taskId, text = text.trim()))
    }

    suspend fun getTaskById(id: Long): Task? = taskDao.getById(id)?.toDomain(
        hasActions = actionDao.countForTask(id) > 0
    )
}

private fun TaskEntity.toDomain(hasActions: Boolean) = Task(
    id = id,
    title = title,
    description = description,
    dueDateEpochDay = dueDateEpochDay,
    completed = completed,
    hasActions = hasActions,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun ActionEntity.toDomain() = TaskAction(
    id = id,
    taskId = taskId,
    text = text,
    createdAt = createdAt
)
