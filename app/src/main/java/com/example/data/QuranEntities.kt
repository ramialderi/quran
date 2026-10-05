package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_progress")
data class UserReadingProgress(
    @PrimaryKey val id: Int = 1,
    val lastPage: Int = 1,
    val nightModeEnabled: Boolean = false,
    val nightShiftAutoEnabled: Boolean = true,
    val nightShiftManualEnabled: Boolean = false,
    val nightShiftWarmth: Float = 0.22f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class UserBookmark(
    @PrimaryKey val page: Int,
    val surahName: String,
    val juzNumber: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_progress")
data class DailyReadingProgress(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val pagesReadCount: Int = 0,
    val targetPagesGoal: Int = 10,
    val readPagesSet: String = "", // Comma-separated distinct pages: "50,51,52"
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "recitation_playlists")
data class RecitationPlaylist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_items")
data class PlaylistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int = 1,
    val reciterId: String,
    val reciterName: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_ayahs")
data class FavoriteAyah(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int,
    val pageNumber: Int,
    val text: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "khatmah_plan")
data class KhatmahPlan(
    @PrimaryKey val id: Int = 1,
    val title: String = "ختمتي المباركة",
    val durationDays: Int = 30, // Default 30 days
    val startPage: Int = 1,
    val endPage: Int = 604,
    val currentProgressPage: Int = 1,
    val startDateTimestamp: Long = System.currentTimeMillis(),
    val dailyReminderHour: Int = 20, // 8:00 PM
    val dailyReminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true,
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)
