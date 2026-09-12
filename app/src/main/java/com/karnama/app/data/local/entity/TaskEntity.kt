package com.karnama.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * یک کار روزانه.
 * dueDateEpochDay: تاریخ (میلادی، به‌صورت epoch day - java.time.LocalDate.toEpochDay())
 * که کار برای آن روز برنامه‌ریزی شده است. نمایش به کاربر همیشه شمسی است،
 * اما ذخیره‌سازی برای مرتب‌سازی و مقایسه‌ی قابل‌اتکا به شکل استاندارد است.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDateEpochDay: Long,
    val completed: Boolean = false,
    val orderIndex: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
