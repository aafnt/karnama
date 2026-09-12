package com.karnama.app.data.repository

/** مدل نمایشی یک کار به همراه اطلاعات کمکی برای UI */
data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val dueDateEpochDay: Long,
    val completed: Boolean,
    val hasActions: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

data class TaskAction(
    val id: Long,
    val taskId: Long,
    val text: String,
    val createdAt: Long
)

data class QuickText(
    val id: Long,
    val text: String,
    val keywords: List<String>
)
