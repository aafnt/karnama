package com.karnama.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * متن آماده‌ی اداری با کلمات کلیدی برای جستجوی سریع هنگام ثبت Task.
 * keywords به‌صورت رشته‌ی جدا شده با ویرگول ذخیره می‌شود (ساده و کافی
 * برای حجم داده‌ی این اپلیکیشن؛ نیازی به جدول جداگانه نیست).
 */
@Entity(tableName = "quick_texts")
data class QuickTextEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val keywords: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun keywordList(): List<String> =
        keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
