package com.karnama.app.data.repository

import androidx.room.withTransaction
import com.karnama.app.data.local.KarnamaDatabase
import com.karnama.app.data.local.entity.ActionEntity
import com.karnama.app.data.local.entity.QuickTextEntity
import com.karnama.app.data.local.entity.TaskEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * پشتیبان‌گیری و بازیابی کامل داده‌ها به‌صورت یک فایل JSON ساده و
 * انسان‌خوان. این کار مستقل از نسخه‌ی برنامه است، پس حتی اگر ساختار
 * دیتابیس در آینده تغییر کند (به همراه Migration مناسب)، بک‌آپ‌های
 * قدیمی همچنان با نگاشت فیلد به فیلد قابل بازیابی می‌مانند.
 *
 * هر دو عملیات با database.withTransaction اجرا می‌شوند: هم برای اینکه
 * (طبق قوانین Room) هرگز روی Main Thread اجرا نشوند و برنامه کرش نکند،
 * و هم برای اینکه بازیابی یک عملیات atomic باشد (اگر وسط راه خطا بیفتد،
 * داده‌های قبلی کاربر نصفه‌و‌نیمه پاک نمی‌شوند).
 */
class BackupRepository(private val database: KarnamaDatabase) {

    suspend fun exportToJson(): String = database.withTransaction {
        val tasks = database.taskDao().getAllForBackup()
        val actions = database.actionDao().getAllForBackup()
        val quickTexts = database.quickTextDao().getAllForBackup()

        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val tasksArray = JSONArray()
        tasks.forEach { t ->
            tasksArray.put(
                JSONObject()
                    .put("id", t.id)
                    .put("title", t.title)
                    .put("description", t.description ?: JSONObject.NULL)
                    .put("dueDateEpochDay", t.dueDateEpochDay)
                    .put("completed", t.completed)
                    .put("orderIndex", t.orderIndex)
                    .put("createdAt", t.createdAt)
                    .put("updatedAt", t.updatedAt)
            )
        }
        root.put("tasks", tasksArray)

        val actionsArray = JSONArray()
        actions.forEach { a ->
            actionsArray.put(
                JSONObject()
                    .put("id", a.id)
                    .put("taskId", a.taskId)
                    .put("text", a.text)
                    .put("createdAt", a.createdAt)
            )
        }
        root.put("actions", actionsArray)

        val quickTextsArray = JSONArray()
        quickTexts.forEach { q ->
            quickTextsArray.put(
                JSONObject()
                    .put("id", q.id)
                    .put("text", q.text)
                    .put("keywords", q.keywords)
                    .put("createdAt", q.createdAt)
                    .put("updatedAt", q.updatedAt)
            )
        }
        root.put("quickTexts", quickTextsArray)

        root.toString(2)
    }

    /**
     * بازیابی از یک فایل JSON. برای جلوگیری از تداخل شناسه‌ها، ابتدا
     * داده‌های فعلی حذف و سپس داده‌های بک‌آپ با همان شناسه‌های اصلی
     * درج می‌شوند.
     */
    suspend fun importFromJson(json: String): Unit = database.withTransaction {
        val root = JSONObject(json)

        database.clearAllTablesForBackup()

        val tasksArray = root.optJSONArray("tasks") ?: JSONArray()
        for (i in 0 until tasksArray.length()) {
            val o = tasksArray.getJSONObject(i)
            database.taskDao().insertForBackup(
                TaskEntity(
                    id = o.getLong("id"),
                    title = o.getString("title"),
                    description = if (o.isNull("description")) null else o.getString("description"),
                    dueDateEpochDay = o.getLong("dueDateEpochDay"),
                    completed = o.getBoolean("completed"),
                    orderIndex = o.optLong("orderIndex", 0),
                    createdAt = o.getLong("createdAt"),
                    updatedAt = o.getLong("updatedAt")
                )
            )
        }

        val actionsArray = root.optJSONArray("actions") ?: JSONArray()
        for (i in 0 until actionsArray.length()) {
            val o = actionsArray.getJSONObject(i)
            database.actionDao().insertForBackup(
                ActionEntity(
                    id = o.getLong("id"),
                    taskId = o.getLong("taskId"),
                    text = o.getString("text"),
                    createdAt = o.getLong("createdAt")
                )
            )
        }

        val quickTextsArray = root.optJSONArray("quickTexts") ?: JSONArray()
        for (i in 0 until quickTextsArray.length()) {
            val o = quickTextsArray.getJSONObject(i)
            database.quickTextDao().insertForBackup(
                QuickTextEntity(
                    id = o.getLong("id"),
                    text = o.getString("text"),
                    keywords = o.optString("keywords", ""),
                    createdAt = o.getLong("createdAt"),
                    updatedAt = o.getLong("updatedAt")
                )
            )
        }
    }
}
