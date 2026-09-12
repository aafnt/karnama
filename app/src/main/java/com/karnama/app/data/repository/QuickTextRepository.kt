package com.karnama.app.data.repository

import com.karnama.app.data.local.dao.QuickTextDao
import com.karnama.app.data.local.entity.QuickTextEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuickTextRepository(private val dao: QuickTextDao) {

    fun observeAll(): Flow<List<QuickText>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    /**
     * جستجوی متن‌های آماده: هم بر اساس شروع خود متن و هم بر اساس هر یک
     * از کلمات کلیدی. کوئری کوتاه‌تر از ۲ کاراکتر نتیجه‌ای برنمی‌گرداند
     * تا لیست پیشنهادها هنگام شروع تایپ شلوغ نشود.
     */
    fun search(all: List<QuickText>, query: String): List<QuickText> {
        val q = query.trim()
        if (q.length < 2) return emptyList()
        return all.filter { qt ->
            qt.text.contains(q, ignoreCase = true) ||
                qt.keywords.any { it.contains(q, ignoreCase = true) }
        }
    }

    suspend fun add(text: String, keywords: List<String>): Long {
        return dao.insert(
            QuickTextEntity(text = text.trim(), keywords = keywords.joinToString(", ") { it.trim() })
        )
    }

    suspend fun update(id: Long, text: String, keywords: List<String>) {
        dao.update(
            QuickTextEntity(
                id = id,
                text = text.trim(),
                keywords = keywords.joinToString(", ") { it.trim() },
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun delete(quickText: QuickText) {
        dao.delete(QuickTextEntity(id = quickText.id, text = quickText.text, keywords = quickText.keywords.joinToString(",")))
    }
}

private fun QuickTextEntity.toDomain() = QuickText(
    id = id,
    text = text,
    keywords = keywordList()
)
