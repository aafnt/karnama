package com.karnama.app.data.local

import androidx.room.migration.Migration

/**
 * محل مرکزی افزودن Migrationهای Room.
 *
 * ── قانون طلایی برای اینکه کاربر هرگز مجبور به Uninstall نشود ──
 * هر زمان ساختار جدول‌ها (Entity) را تغییر دادید:
 *   ۱) شماره `version` را در KarnamaDatabase.kt یک واحد افزایش دهید.
 *   ۲) یک Migration جدید از نسخه‌ی قبلی به نسخه‌ی جدید اینجا اضافه کنید
 *      (مثلاً با ALTER TABLE برای ستون جدید).
 *   ۳) Migration را به لیست ALL_MIGRATIONS اضافه کنید.
 *
 * هرگز از fallbackToDestructiveMigration() در KarnamaDatabase استفاده
 * نکنید؛ چون آن باعث پاک شدن کامل داده‌های کاربر هنگام آپدیت می‌شود.
 * ندادن Migration لازم باعث Crash در باز شدن دیتابیس می‌شود (که خودش
 * یک هشدار امن است تا یادتان بماند Migration بنویسید) نه پاک شدن داده.
 *
 * نمونه (وقتی ستون priority به جدول tasks اضافه شود):
 *
 * val MIGRATION_1_2 = object : Migration(1, 2) {
 *     override fun migrate(db: SupportSQLiteDatabase) {
 *         db.execSQL("ALTER TABLE tasks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
 *     }
 * }
 *
 * سپس: val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
 */
val ALL_MIGRATIONS: Array<Migration> = arrayOf(
    // Migrationهای آینده اینجا اضافه می‌شوند.
)
