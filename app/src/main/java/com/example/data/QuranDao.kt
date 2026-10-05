package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    @Query("SELECT * FROM reading_progress WHERE id = 1 LIMIT 1")
    fun getReadingProgress(): Flow<UserReadingProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: UserReadingProgress)

    @Query("SELECT * FROM bookmarks ORDER BY page ASC")
    fun getAllBookmarks(): Flow<List<UserBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: UserBookmark)

    @Query("DELETE FROM bookmarks WHERE page = :page")
    suspend fun removeBookmark(page: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE page = :page)")
    fun isPageBookmarked(page: Int): Flow<Boolean>

    @Query("SELECT * FROM daily_progress WHERE date = :date LIMIT 1")
    fun getDailyProgress(date: String): Flow<DailyReadingProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyProgress(progress: DailyReadingProgress)

    @Query("SELECT * FROM daily_progress ORDER BY date DESC LIMIT 30")
    fun getRecentDailyProgress(): Flow<List<DailyReadingProgress>>

    // Playlists Queries
    @Query("SELECT * FROM recitation_playlists ORDER BY id ASC")
    fun getAllPlaylists(): Flow<List<RecitationPlaylist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: RecitationPlaylist): Long

    @Query("DELETE FROM recitation_playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId ORDER BY id ASC")
    fun getPlaylistItems(playlistId: Long): Flow<List<PlaylistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(item: PlaylistItem): Long

    @Query("DELETE FROM playlist_items WHERE id = :itemId")
    suspend fun deletePlaylistItem(itemId: Long)

    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId")
    suspend fun deleteItemsForPlaylist(playlistId: Long)

    // Favorite Ayahs Queries
    @Query("SELECT * FROM favorite_ayahs ORDER BY id DESC")
    fun getAllFavoriteAyahs(): Flow<List<FavoriteAyah>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_ayahs WHERE surahId = :surahId AND ayahNumber = :ayahNumber)")
    fun isAyahFavorite(surahId: Int, ayahNumber: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteAyah(item: FavoriteAyah): Long

    @Query("DELETE FROM favorite_ayahs WHERE surahId = :surahId AND ayahNumber = :ayahNumber")
    suspend fun deleteFavoriteAyahBySurahAndNumber(surahId: Int, ayahNumber: Int)

    @Query("DELETE FROM favorite_ayahs WHERE id = :id")
    suspend fun deleteFavoriteAyahById(id: Long)

    // Khatmah Plan Queries
    @Query("SELECT * FROM khatmah_plan WHERE id = 1 LIMIT 1")
    fun getKhatmahPlan(): Flow<KhatmahPlan?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKhatmahPlan(plan: KhatmahPlan)

    @Query("UPDATE khatmah_plan SET currentProgressPage = :page, lastUpdated = :timestamp WHERE id = 1")
    suspend fun updateKhatmahProgress(page: Int, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM khatmah_plan WHERE id = 1")
    suspend fun resetKhatmahPlan()
}
