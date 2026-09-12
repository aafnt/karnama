package com.karnama.app.di

import android.content.Context
import com.karnama.app.data.local.KarnamaDatabase
import com.karnama.app.data.repository.BackupRepository
import com.karnama.app.data.repository.QuickTextRepository
import com.karnama.app.data.repository.TaskRepository
import com.karnama.app.settings.SettingsRepository

/**
 * Container ساده و دستی برای وابستگی‌ها (به‌جای Hilt/Koin).
 * برای اندازه‌ی این پروژه، Dependency Injection دستی هم ساده‌تر است
 * و هم شفاف‌تر، و در صورت رشد پروژه به‌راحتی قابل مهاجرت به Hilt است.
 */
class AppContainer(context: Context) {
    private val database = KarnamaDatabase.getInstance(context)

    val taskRepository = TaskRepository(database.taskDao(), database.actionDao())
    val quickTextRepository = QuickTextRepository(database.quickTextDao())
    val settingsRepository = SettingsRepository(context)
    val backupRepository = BackupRepository(database)
}
