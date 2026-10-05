package com.example.data

import kotlinx.coroutines.flow.Flow

class QuranRepository(private val dao: QuranDao) {
    val readingProgress: Flow<UserReadingProgress?> = dao.getReadingProgress()
    val allBookmarks: Flow<List<UserBookmark>> = dao.getAllBookmarks()

    suspend fun saveProgress(
        page: Int,
        nightMode: Boolean,
        nightShiftAuto: Boolean = true,
        nightShiftManual: Boolean = false,
        nightShiftWarmth: Float = 0.22f
    ) {
        dao.saveReadingProgress(
            UserReadingProgress(
                id = 1,
                lastPage = page,
                nightModeEnabled = nightMode,
                nightShiftAutoEnabled = nightShiftAuto,
                nightShiftManualEnabled = nightShiftManual,
                nightShiftWarmth = nightShiftWarmth,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleBookmark(page: Int, surahName: String, juzNumber: Int, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            dao.removeBookmark(page)
        } else {
            dao.addBookmark(
                UserBookmark(
                    page = page,
                    surahName = surahName,
                    juzNumber = juzNumber
                )
            )
        }
    }

    fun isBookmarked(page: Int): Flow<Boolean> = dao.isPageBookmarked(page)

    fun getDailyProgress(date: String): Flow<DailyReadingProgress?> = dao.getDailyProgress(date)
    fun getRecentDailyProgress(): Flow<List<DailyReadingProgress>> = dao.getRecentDailyProgress()

    suspend fun recordPageRead(page: Int, date: String, currentProgress: DailyReadingProgress?): DailyReadingProgress {
        val existingGoal = currentProgress?.targetPagesGoal ?: 10
        val existingPages = currentProgress?.readPagesSet
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toMutableSet() ?: mutableSetOf()

        existingPages.add(page.toString())
        val updated = DailyReadingProgress(
            date = date,
            pagesReadCount = existingPages.size,
            targetPagesGoal = existingGoal,
            readPagesSet = existingPages.joinToString(","),
            lastUpdated = System.currentTimeMillis()
        )
        dao.saveDailyProgress(updated)
        return updated
    }

    suspend fun updateDailyGoal(date: String, newGoal: Int, currentProgress: DailyReadingProgress?) {
        val updated = (currentProgress ?: DailyReadingProgress(date = date)).copy(
            targetPagesGoal = newGoal,
            lastUpdated = System.currentTimeMillis()
        )
        dao.saveDailyProgress(updated)
    }

    // Playlist repository methods
    val allPlaylists: Flow<List<RecitationPlaylist>> = dao.getAllPlaylists()

    fun getPlaylistItems(playlistId: Long): Flow<List<PlaylistItem>> = dao.getPlaylistItems(playlistId)

    suspend fun createPlaylist(name: String, description: String = ""): Long {
        return dao.insertPlaylist(RecitationPlaylist(name = name, description = description))
    }

    suspend fun deletePlaylist(playlistId: Long) {
        dao.deleteItemsForPlaylist(playlistId)
        dao.deletePlaylist(playlistId)
    }

    suspend fun addItemToPlaylist(
        playlistId: Long,
        surahId: Int,
        surahName: String,
        ayahNumber: Int,
        reciterId: String,
        reciterName: String
    ): Long {
        return dao.insertPlaylistItem(
            PlaylistItem(
                playlistId = playlistId,
                surahId = surahId,
                surahName = surahName,
                ayahNumber = ayahNumber,
                reciterId = reciterId,
                reciterName = reciterName
            )
        )
    }

    suspend fun removePlaylistItem(itemId: Long) {
        dao.deletePlaylistItem(itemId)
    }

    // Favorite Ayahs repository methods
    val allFavoriteAyahs: Flow<List<FavoriteAyah>> = dao.getAllFavoriteAyahs()

    fun isAyahFavorite(surahId: Int, ayahNumber: Int): Flow<Boolean> =
        dao.isAyahFavorite(surahId, ayahNumber)

    suspend fun toggleFavoriteAyah(
        surahId: Int,
        surahName: String,
        ayahNumber: Int,
        pageNumber: Int,
        text: String,
        isFavorite: Boolean
    ) {
        if (isFavorite) {
            dao.deleteFavoriteAyahBySurahAndNumber(surahId, ayahNumber)
        } else {
            dao.insertFavoriteAyah(
                FavoriteAyah(
                    surahId = surahId,
                    surahName = surahName,
                    ayahNumber = ayahNumber,
                    pageNumber = pageNumber,
                    text = text
                )
            )
        }
    }

    suspend fun deleteFavoriteAyahById(id: Long) {
        dao.deleteFavoriteAyahById(id)
    }

    // Khatmah Plan repository methods
    val khatmahPlan: Flow<KhatmahPlan?> = dao.getKhatmahPlan()

    suspend fun saveKhatmahPlan(plan: KhatmahPlan) {
        dao.saveKhatmahPlan(plan)
    }

    suspend fun updateKhatmahProgress(page: Int) {
        dao.updateKhatmahProgress(page)
    }

    suspend fun resetKhatmahPlan() {
        dao.resetKhatmahPlan()
    }
}
