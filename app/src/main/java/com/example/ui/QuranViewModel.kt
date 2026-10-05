package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerState
import com.example.audio.QuranAudioPlayer
import com.example.audio.Reciter
import com.example.audio.RecitersData
import com.example.data.DailyReadingProgress
import com.example.data.FavoriteAyah
import com.example.data.KhatmahPlan
import com.example.data.PlaylistItem
import com.example.data.QuranData
import com.example.data.QuranDatabase
import com.example.data.QuranPage
import com.example.data.QuranRepository
import com.example.data.RecitationPlaylist
import com.example.data.SurahInfo
import com.example.data.TafsirRepository
import com.example.data.TafsirResult
import com.example.data.UserBookmark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

data class QuranUiState(
    val currentPageNumber: Int = 50,
    val isOverviewMode: Boolean = false,
    val isZoomedMode: Boolean = false,
    val isNightMode: Boolean = false,
    val isNightShiftDialogOpen: Boolean = false,
    val nightShiftAutoEnabled: Boolean = true,
    val nightShiftManualEnabled: Boolean = false,
    val nightShiftWarmth: Float = 0.22f,
    val isIndexOpen: Boolean = false,
    val isBookmarksOpen: Boolean = false,
    val isSearchOpen: Boolean = false,
    val isGoToPageOpen: Boolean = false,
    val isReciterDialogOpen: Boolean = false,
    val isTafsirOpen: Boolean = false,
    val isDailyProgressOpen: Boolean = false,
    val isPlaylistsOpen: Boolean = false,
    val isAddToPlaylistOpen: Boolean = false,
    val isKhatmahDialogOpen: Boolean = false,
    val isKhatmahReminderBannerVisible: Boolean = false,
    val currentTafsir: TafsirResult? = null,
    val dailyProgress: DailyReadingProgress? = null,
    val khatmahPlan: KhatmahPlan? = null,
    val streakDays: Int = 1,
    val isBookmarked: Boolean = false,
    val selectedAyahKey: String? = null,
    val bookmarks: List<UserBookmark> = emptyList(),
    val favoriteAyahs: List<FavoriteAyah> = emptyList(),
    val playlists: List<RecitationPlaylist> = emptyList(),
    val selectedPlaylist: RecitationPlaylist? = null,
    val selectedPlaylistItems: List<PlaylistItem> = emptyList()
) {
    val isNightShiftActive: Boolean
        get() {
            if (nightShiftManualEnabled) return true
            if (!nightShiftAutoEnabled) return false
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return currentHour >= 19 || currentHour < 6
        }

    val dailyRequiredPages: Int
        get() {
            val plan = khatmahPlan ?: return 20
            return ceil(604.0 / plan.durationDays).toInt().coerceAtLeast(1)
        }

    val khatmahPagesLeftToday: Int
        get() {
            val target = dailyRequiredPages
            val read = dailyProgress?.pagesReadCount ?: 0
            return (target - read).coerceAtLeast(0)
        }
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository
    private val tafsirRepository = TafsirRepository()
    val audioPlayer = QuranAudioPlayer(application)
    val playerState: StateFlow<AudioPlayerState> = audioPlayer.playerState

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private val todayDateString: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

    init {
        val database = QuranDatabase.getDatabase(application)
        repository = QuranRepository(database.quranDao())

        // Load saved reading progress and bookmarks on startup
        viewModelScope.launch {
            val progress = repository.readingProgress.firstOrNull()
            if (progress != null) {
                _uiState.value = _uiState.value.copy(
                    currentPageNumber = progress.lastPage.coerceIn(1, 604),
                    isNightMode = progress.nightModeEnabled,
                    nightShiftAutoEnabled = progress.nightShiftAutoEnabled,
                    nightShiftManualEnabled = progress.nightShiftManualEnabled,
                    nightShiftWarmth = progress.nightShiftWarmth
                )
            }

            // Record initial page as read for today
            val initialPage = _uiState.value.currentPageNumber
            repository.recordPageRead(initialPage, todayDateString, null)

            // Observe bookmarks
            launch {
                repository.allBookmarks.collect { bookmarksList ->
                    val currentPg = _uiState.value.currentPageNumber
                    val isCurrentMarked = bookmarksList.any { it.page == currentPg }
                    _uiState.value = _uiState.value.copy(
                        bookmarks = bookmarksList,
                        isBookmarked = isCurrentMarked
                    )
                }
            }

            // Observe favorite ayahs and seed initial favorites if empty
            launch {
                repository.allFavoriteAyahs.collect { favList ->
                    if (favList.isEmpty()) {
                        repository.toggleFavoriteAyah(
                            surahId = 2,
                            surahName = "سورة البقرة",
                            ayahNumber = 255,
                            pageNumber = 42,
                            text = "ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلۡحَيُّ ٱلۡقَيُّومُۚ لَا تَأۡخُذُهُۥ سِنَةٞ وَلَا نَوۡمٞۚ",
                            isFavorite = false
                        )
                        repository.toggleFavoriteAyah(
                            surahId = 1,
                            surahName = "سورة الفاتحة",
                            ayahNumber = 1,
                            pageNumber = 1,
                            text = "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
                            isFavorite = false
                        )
                        repository.toggleFavoriteAyah(
                            surahId = 112,
                            surahName = "سورة الإخلاص",
                            ayahNumber = 1,
                            pageNumber = 604,
                            text = "قُلۡ هُوَ ٱللَّهُ أَحَدٌ",
                            isFavorite = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(favoriteAyahs = favList)
                    }
                }
            }

            // Observe Khatmah Plan and seed default 30-day plan if empty
            launch {
                repository.khatmahPlan.collect { plan ->
                    if (plan == null) {
                        val defaultPlan = KhatmahPlan(
                            id = 1,
                            title = "ختمتي المباركة",
                            durationDays = 30,
                            startPage = 1,
                            endPage = 604,
                            currentProgressPage = _uiState.value.currentPageNumber,
                            startDateTimestamp = System.currentTimeMillis(),
                            dailyReminderHour = 20,
                            dailyReminderMinute = 0,
                            isReminderEnabled = true,
                            isActive = true
                        )
                        repository.saveKhatmahPlan(defaultPlan)
                    } else {
                        val requiredPages = ceil(604.0 / plan.durationDays).toInt()
                        val pagesRead = _uiState.value.dailyProgress?.pagesReadCount ?: 0
                        val shouldShowBanner = plan.isReminderEnabled && plan.isActive && (requiredPages - pagesRead > 0)

                        _uiState.value = _uiState.value.copy(
                            khatmahPlan = plan,
                            isKhatmahReminderBannerVisible = shouldShowBanner
                        )
                    }
                }
            }

            // Observe today's daily progress
            launch {
                repository.getDailyProgress(todayDateString).collect { daily ->
                    _uiState.value = _uiState.value.copy(
                        dailyProgress = daily ?: DailyReadingProgress(date = todayDateString)
                    )
                }
            }

            // Observe recent progress to calculate streak
            launch {
                repository.getRecentDailyProgress().collect { recentList ->
                    val calculatedStreak = calculateStreak(recentList)
                    _uiState.value = _uiState.value.copy(
                        streakDays = calculatedStreak
                    )
                }
            }

            // Observe audio playlists and seed default presets if empty
            launch {
                repository.allPlaylists.collect { list ->
                    if (list.isEmpty()) {
                        val p1 = repository.createPlaylist("تلاوات خاشعة ومفضلة", "مجموعة من التلاوات المباركة لأشهر القراء")
                        repository.addItemToPlaylist(p1, 1, "سورة الفاتحة", 1, "abdulbasit", "عبد الباسط عبد الصمد")
                        repository.addItemToPlaylist(p1, 19, "سورة مريم", 1, "minshawy", "محمد صديق المنشاوي")

                        val p2 = repository.createPlaylist("سورة الكهف - الجمعة المباركة", "قراءات لسورة الكهف المباركة")
                        repository.addItemToPlaylist(p2, 18, "سورة الكهف", 1, "alafasy", "مشاري بن راشد العفاسي")

                        val p3 = repository.createPlaylist("أذكار وتلاوات السكينة", "تلاوات هادئة للراحة والسكينة")
                        repository.addItemToPlaylist(p3, 55, "سورة الرحمن", 1, "husary", "محمود خليل الحصري")
                        repository.addItemToPlaylist(p3, 67, "سورة الملك", 1, "alafasy", "مشاري بن راشد العفاسي")
                    } else {
                        _uiState.value = _uiState.value.copy(playlists = list)
                    }
                }
            }
        }
    }

    private fun calculateStreak(records: List<DailyReadingProgress>): Int {
        if (records.isEmpty()) return 1
        var streak = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()

        // Check recent days
        for (i in 0..60) {
            val dateStr = sdf.format(cal.time)
            val match = records.find { it.date == dateStr }
            if (match != null && match.pagesReadCount > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else if (i == 0) {
                // If today has 0 pages yet, check yesterday
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak.coerceAtLeast(1)
    }

    fun onPageChanged(page: Int) {
        val safePage = page.coerceIn(1, 604)
        if (_uiState.value.currentPageNumber == safePage) return

        val isMarked = _uiState.value.bookmarks.any { it.page == safePage }
        _uiState.value = _uiState.value.copy(
            currentPageNumber = safePage,
            isBookmarked = isMarked
        )

        // Persist immediately to Room
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveProgress(
                page = safePage,
                nightMode = s.isNightMode,
                nightShiftAuto = s.nightShiftAutoEnabled,
                nightShiftManual = s.nightShiftManualEnabled,
                nightShiftWarmth = s.nightShiftWarmth
            )
            // Track page as read today
            repository.recordPageRead(safePage, todayDateString, _uiState.value.dailyProgress)

            // Update Khatmah progress page if advanced
            val plan = s.khatmahPlan
            if (plan != null && plan.isActive && safePage > plan.currentProgressPage) {
                repository.updateKhatmahProgress(safePage)
            }
        }
    }

    fun toggleOverviewMode() {
        _uiState.value = _uiState.value.copy(
            isOverviewMode = !_uiState.value.isOverviewMode
        )
    }

    fun toggleZoomMode() {
        _uiState.value = _uiState.value.copy(
            isZoomedMode = !_uiState.value.isZoomedMode
        )
    }

    fun setOverviewMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isOverviewMode = enabled)
    }

    fun selectSurah(surah: SurahInfo) {
        onPageChanged(surah.startPage)
    }

    fun toggleNightMode() {
        val newMode = !_uiState.value.isNightMode
        _uiState.value = _uiState.value.copy(isNightMode = newMode)
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveProgress(
                page = s.currentPageNumber,
                nightMode = newMode,
                nightShiftAuto = s.nightShiftAutoEnabled,
                nightShiftManual = s.nightShiftManualEnabled,
                nightShiftWarmth = s.nightShiftWarmth
            )
        }
    }

    // Night Shift mode controls
    fun openNightShiftDialog() {
        _uiState.value = _uiState.value.copy(isNightShiftDialogOpen = true)
    }

    fun closeNightShiftDialog() {
        _uiState.value = _uiState.value.copy(isNightShiftDialogOpen = false)
    }

    fun toggleNightShiftAuto(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(nightShiftAutoEnabled = enabled)
        persistNightShift()
    }

    fun toggleNightShiftManual(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(nightShiftManualEnabled = enabled)
        persistNightShift()
    }

    fun updateNightShiftWarmth(warmth: Float) {
        _uiState.value = _uiState.value.copy(nightShiftWarmth = warmth)
        persistNightShift()
    }

    private fun persistNightShift() {
        val s = _uiState.value
        viewModelScope.launch {
            repository.saveProgress(
                page = s.currentPageNumber,
                nightMode = s.isNightMode,
                nightShiftAuto = s.nightShiftAutoEnabled,
                nightShiftManual = s.nightShiftManualEnabled,
                nightShiftWarmth = s.nightShiftWarmth
            )
        }
    }

    fun toggleBookmark() {
        val page = _uiState.value.currentPageNumber
        val isCurrentlyMarked = _uiState.value.isBookmarked
        val surah = QuranData.getSurahForPage(page)
        val juz = QuranData.getJuzNumberForPage(page)

        viewModelScope.launch {
            repository.toggleBookmark(
                page = page,
                surahName = surah.fullName,
                juzNumber = juz,
                isCurrentlyBookmarked = isCurrentlyMarked
            )
            _uiState.value = _uiState.value.copy(isBookmarked = !isCurrentlyMarked)
        }
    }

    fun deleteBookmark(page: Int) {
        val surah = QuranData.getSurahForPage(page)
        val juz = QuranData.getJuzNumberForPage(page)
        viewModelScope.launch {
            repository.toggleBookmark(
                page = page,
                surahName = surah.fullName,
                juzNumber = juz,
                isCurrentlyBookmarked = true
            )
        }
    }

    // Favorite Ayahs actions
    fun isAyahFavorite(surahId: Int, ayahNumber: Int): Boolean {
        return _uiState.value.favoriteAyahs.any { it.surahId == surahId && it.ayahNumber == ayahNumber }
    }

    fun toggleFavoriteAyah(surahId: Int, ayahNumber: Int) {
        val isFav = isAyahFavorite(surahId, ayahNumber)
        val surah = QuranData.surahs.find { it.id == surahId } ?: return

        var pageNum = surah.startPage
        var verseText = "﴿ ${surah.fullName} - آية ${QuranData.toArabicDigits(ayahNumber)} ﴾"
        for (p in surah.startPage..surah.endPage) {
            val pageData = QuranData.getPage(p)
            val v = pageData.verses.find { it.ayahNumber == ayahNumber }
            if (v != null) {
                pageNum = p
                verseText = v.text
                break
            }
        }

        viewModelScope.launch {
            repository.toggleFavoriteAyah(
                surahId = surahId,
                surahName = surah.fullName,
                ayahNumber = ayahNumber,
                pageNumber = pageNum,
                text = verseText,
                isFavorite = isFav
            )
        }
    }

    fun deleteFavoriteAyah(id: Long) {
        viewModelScope.launch {
            repository.deleteFavoriteAyahById(id)
        }
    }

    fun navigateToFavoriteAyah(item: FavoriteAyah) {
        onPageChanged(item.pageNumber)
        _uiState.value = _uiState.value.copy(
            selectedAyahKey = "${item.surahId}:${item.ayahNumber}",
            isBookmarksOpen = false
        )
    }

    fun playFavoriteAyah(item: FavoriteAyah) {
        onPageChanged(item.pageNumber)
        playAyahDirectly(item.surahId, item.ayahNumber)
    }

    // Khatmah tracking methods
    fun openKhatmahDialog() {
        _uiState.value = _uiState.value.copy(isKhatmahDialogOpen = true)
    }

    fun closeKhatmahDialog() {
        _uiState.value = _uiState.value.copy(isKhatmahDialogOpen = false)
    }

    fun startOrUpdateKhatmah(durationDays: Int, reminderHour: Int, reminderMinute: Int, reminderEnabled: Boolean) {
        viewModelScope.launch {
            val existing = _uiState.value.khatmahPlan
            val newPlan = KhatmahPlan(
                id = 1,
                title = "ختمتي المباركة",
                durationDays = durationDays,
                startPage = 1,
                endPage = 604,
                currentProgressPage = existing?.currentProgressPage ?: _uiState.value.currentPageNumber,
                startDateTimestamp = existing?.startDateTimestamp ?: System.currentTimeMillis(),
                dailyReminderHour = reminderHour,
                dailyReminderMinute = reminderMinute,
                isReminderEnabled = reminderEnabled,
                isActive = true
            )
            repository.saveKhatmahPlan(newPlan)
        }
    }

    fun resetKhatmah() {
        viewModelScope.launch {
            val freshPlan = KhatmahPlan(
                id = 1,
                title = "ختمتي المباركة",
                durationDays = 30,
                startPage = 1,
                endPage = 604,
                currentProgressPage = 1,
                startDateTimestamp = System.currentTimeMillis(),
                dailyReminderHour = 20,
                dailyReminderMinute = 0,
                isReminderEnabled = true,
                isActive = true
            )
            repository.saveKhatmahPlan(freshPlan)
        }
    }

    fun dismissKhatmahReminderBanner() {
        _uiState.value = _uiState.value.copy(isKhatmahReminderBannerVisible = false)
    }

    fun openIndex() {
        _uiState.value = _uiState.value.copy(isIndexOpen = true)
    }

    fun closeIndex() {
        _uiState.value = _uiState.value.copy(isIndexOpen = false)
    }

    fun openBookmarks() {
        _uiState.value = _uiState.value.copy(isBookmarksOpen = true)
    }

    fun closeBookmarks() {
        _uiState.value = _uiState.value.copy(isBookmarksOpen = false)
    }

    fun openSearch() {
        _uiState.value = _uiState.value.copy(isSearchOpen = true)
    }

    fun closeSearch() {
        _uiState.value = _uiState.value.copy(isSearchOpen = false)
    }

    fun openGoToPage() {
        _uiState.value = _uiState.value.copy(isGoToPageOpen = true)
    }

    fun closeGoToPage() {
        _uiState.value = _uiState.value.copy(isGoToPageOpen = false)
    }

    // Daily Progress actions
    fun openDailyProgress() {
        _uiState.value = _uiState.value.copy(isDailyProgressOpen = true)
    }

    fun closeDailyProgress() {
        _uiState.value = _uiState.value.copy(isDailyProgressOpen = false)
    }

    fun updateDailyGoal(newGoal: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(todayDateString, newGoal, _uiState.value.dailyProgress)
        }
    }

    // Audio Playlists actions
    fun openPlaylists() {
        _uiState.value = _uiState.value.copy(isPlaylistsOpen = true, selectedPlaylist = null)
    }

    fun closePlaylists() {
        _uiState.value = _uiState.value.copy(isPlaylistsOpen = false, selectedPlaylist = null)
    }

    fun openAddToPlaylist() {
        _uiState.value = _uiState.value.copy(isAddToPlaylistOpen = true)
    }

    fun closeAddToPlaylist() {
        _uiState.value = _uiState.value.copy(isAddToPlaylistOpen = false)
    }

    fun selectPlaylist(playlist: RecitationPlaylist) {
        _uiState.value = _uiState.value.copy(selectedPlaylist = playlist)
        viewModelScope.launch {
            repository.getPlaylistItems(playlist.id).collect { items ->
                _uiState.value = _uiState.value.copy(selectedPlaylistItems = items)
            }
        }
    }

    fun backToPlaylists() {
        _uiState.value = _uiState.value.copy(selectedPlaylist = null, selectedPlaylistItems = emptyList())
    }

    fun createPlaylist(name: String, description: String = "") {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
            if (_uiState.value.selectedPlaylist?.id == playlistId) {
                backToPlaylists()
            }
        }
    }

    fun addCurrentRecitationToPlaylist(playlistId: Long) {
        val currentState = playerState.value
        val surah = QuranData.surahs.find { it.id == currentState.currentSurah } ?: QuranData.surahs[0]
        val reciter = currentState.currentReciter
        viewModelScope.launch {
            repository.addItemToPlaylist(
                playlistId = playlistId,
                surahId = surah.id,
                surahName = surah.fullName,
                ayahNumber = currentState.currentAyah,
                reciterId = reciter.id,
                reciterName = reciter.name
            )
        }
    }

    fun createPlaylistAndAddCurrent(name: String) {
        val currentState = playerState.value
        val surah = QuranData.surahs.find { it.id == currentState.currentSurah } ?: QuranData.surahs[0]
        val reciter = currentState.currentReciter
        viewModelScope.launch {
            val newId = repository.createPlaylist(name)
            repository.addItemToPlaylist(
                playlistId = newId,
                surahId = surah.id,
                surahName = surah.fullName,
                ayahNumber = currentState.currentAyah,
                reciterId = reciter.id,
                reciterName = reciter.name
            )
        }
    }

    fun deletePlaylistItem(itemId: Long) {
        viewModelScope.launch {
            repository.removePlaylistItem(itemId)
        }
    }

    fun playPlaylistItem(item: PlaylistItem) {
        val reciter = RecitersData.reciters.find { it.id == item.reciterId }
            ?: RecitersData.defaultReciter
        setReciter(reciter)

        val surah = QuranData.surahs.find { it.id == item.surahId }
        if (surah != null) {
            onPageChanged(surah.startPage)
        }
        playAyahDirectly(item.surahId, item.ayahNumber)
        closePlaylists()
    }

    fun toggleAyahSelection(surah: Int, ayah: Int) {
        val key = "$surah:$ayah"
        val currentKey = _uiState.value.selectedAyahKey
        val newKey = if (currentKey == key) null else key
        _uiState.value = _uiState.value.copy(selectedAyahKey = newKey)

        if (newKey != null && audioPlayer.playerState.value.isPlayerVisible) {
            playAyahDirectly(surah, ayah)
        }
    }

    fun clearAyahSelection() {
        if (_uiState.value.selectedAyahKey != null) {
            _uiState.value = _uiState.value.copy(selectedAyahKey = null)
        }
    }

    // Audio Playback functions
    fun playCurrentAyah() {
        val currentPg = _uiState.value.currentPageNumber
        val pageData = QuranData.getPage(currentPg)
        val surah = QuranData.getSurahForPage(currentPg)

        val selectedParts = _uiState.value.selectedAyahKey?.split(":")
        val selSurahId = selectedParts?.getOrNull(0)?.toIntOrNull()
        val selAyahNum = selectedParts?.getOrNull(1)?.toIntOrNull()

        val (targetSurahId, targetAyahNum) = if (selSurahId == surah.id && selAyahNum != null) {
            Pair(selSurahId, selAyahNum)
        } else {
            val firstAyah = pageData.verses.firstOrNull()?.ayahNumber ?: 1
            Pair(surah.id, firstAyah)
        }

        playAyahDirectly(targetSurahId, targetAyahNum)
    }

    fun playAyahDirectly(surahId: Int, ayahNum: Int) {
        _uiState.value = _uiState.value.copy(
            selectedAyahKey = "$surahId:$ayahNum"
        )
        audioPlayer.playAyah(surahId, ayahNum) {
            playNextAyah()
        }
    }

    fun playNextAyah() {
        val currentState = audioPlayer.playerState.value
        val surahInfo = QuranData.surahs.find { it.id == currentState.currentSurah } ?: QuranData.surahs[0]

        val nextAyahNum = currentState.currentAyah + 1
        if (nextAyahNum <= surahInfo.ayahsCount) {
            playAyahDirectly(surahInfo.id, nextAyahNum)
        } else {
            val nextSurahIndex = QuranData.surahs.indexOf(surahInfo) + 1
            if (nextSurahIndex < QuranData.surahs.size) {
                val nextSurah = QuranData.surahs[nextSurahIndex]
                onPageChanged(nextSurah.startPage)
                playAyahDirectly(nextSurah.id, 1)
            } else {
                audioPlayer.stopAndHide()
            }
        }
    }

    fun playPreviousAyah() {
        val currentState = audioPlayer.playerState.value
        val surahInfo = QuranData.surahs.find { it.id == currentState.currentSurah } ?: QuranData.surahs[0]

        val prevAyahNum = currentState.currentAyah - 1
        if (prevAyahNum >= 1) {
            playAyahDirectly(surahInfo.id, prevAyahNum)
        } else {
            val prevSurahIndex = QuranData.surahs.indexOf(surahInfo) - 1
            if (prevSurahIndex >= 0) {
                val prevSurah = QuranData.surahs[prevSurahIndex]
                playAyahDirectly(prevSurah.id, prevSurah.ayahsCount)
            }
        }
    }

    fun toggleAudioPlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun setReciter(reciter: Reciter) {
        audioPlayer.setReciter(reciter)
    }

    fun openReciterDialog() {
        _uiState.value = _uiState.value.copy(isReciterDialogOpen = true)
    }

    fun closeReciterDialog() {
        _uiState.value = _uiState.value.copy(isReciterDialogOpen = false)
    }

    fun closeAudioPlayer() {
        audioPlayer.stopAndHide()
        clearAyahSelection()
    }

    // Tafsir functions
    fun openTafsirForAyah(surahId: Int, ayahNumber: Int) {
        val surah = QuranData.surahs.find { it.id == surahId }
        val surahName = surah?.fullName ?: "سورة"

        // Highlight/shade the ayah when long-pressed
        _uiState.value = _uiState.value.copy(
            selectedAyahKey = "$surahId:$ayahNumber",
            isTafsirOpen = true,
            currentTafsir = TafsirResult(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahName = surahName,
                tafsirName = "التفسير الميسر (مجمع الملك فهد)",
                text = "",
                isLoading = true
            )
        )

        viewModelScope.launch {
            val result = tafsirRepository.getTafsir(surahId, ayahNumber)
            _uiState.value = _uiState.value.copy(
                currentTafsir = result
            )
        }
    }

    fun closeTafsir() {
        _uiState.value = _uiState.value.copy(
            isTafsirOpen = false,
            currentTafsir = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
