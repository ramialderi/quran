package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.AudioPlayerBar
import com.example.ui.components.AyahActionBar
import com.example.ui.components.BookmarksSheet
import com.example.ui.components.BottomScrubber
import com.example.ui.components.DailyProgressBadge
import com.example.ui.components.DailyProgressDialog
import com.example.ui.components.GoToPageDialog
import com.example.ui.components.KhatmahDialog
import com.example.ui.components.NightShiftDialog
import com.example.ui.components.PlaylistsSheet
import com.example.ui.components.QuranPageCard
import com.example.ui.components.QuranSearchSheet
import com.example.ui.components.QuranSideDrawer
import com.example.ui.components.ReciterSelectionDialog
import com.example.ui.components.SurahIndexSheet
import com.example.ui.components.TafsirBottomSheet
import com.example.ui.components.TopSurahCarousel
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranDarkGreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranNightBackground
import com.example.ui.theme.QuranParchment
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val scope = rememberCoroutineScope()

    val totalPages = 604
    val initialPage = (uiState.currentPageNumber - 1).coerceIn(0, totalPages - 1)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { totalPages }
    )

    // Sync Pager swipes back to ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { index ->
            val pageNum = index + 1
            if (pageNum != uiState.currentPageNumber) {
                viewModel.clearAyahSelection()
                viewModel.onPageChanged(pageNum)
            }
        }
    }

    // When currentPageNumber changes from top carousel, scrubber or index, scroll the pager
    LaunchedEffect(uiState.currentPageNumber) {
        val targetIndex = uiState.currentPageNumber - 1
        if (pagerState.currentPage != targetIndex) {
            pagerState.animateScrollToPage(targetIndex)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Back handler: if drawer, dialogs, audio player or sheets are open, handle back gracefully
    BackHandler(enabled = drawerState.isOpen || uiState.isKhatmahDialogOpen || uiState.isSearchOpen || uiState.isNightShiftDialogOpen || uiState.isAddToPlaylistOpen || uiState.isPlaylistsOpen || uiState.isDailyProgressOpen || uiState.isTafsirOpen || uiState.isReciterDialogOpen || playerState.isPlayerVisible || uiState.selectedAyahKey != null || uiState.isOverviewMode || uiState.isIndexOpen || uiState.isBookmarksOpen || uiState.isGoToPageOpen) {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            uiState.isKhatmahDialogOpen -> viewModel.closeKhatmahDialog()
            uiState.isSearchOpen -> viewModel.closeSearch()
            uiState.isNightShiftDialogOpen -> viewModel.closeNightShiftDialog()
            uiState.isAddToPlaylistOpen -> viewModel.closeAddToPlaylist()
            uiState.isPlaylistsOpen -> viewModel.closePlaylists()
            uiState.isDailyProgressOpen -> viewModel.closeDailyProgress()
            uiState.isTafsirOpen -> viewModel.closeTafsir()
            uiState.isReciterDialogOpen -> viewModel.closeReciterDialog()
            playerState.isPlayerVisible -> viewModel.closeAudioPlayer()
            uiState.selectedAyahKey != null -> viewModel.clearAyahSelection()
            uiState.isGoToPageOpen -> viewModel.closeGoToPage()
            uiState.isIndexOpen -> viewModel.closeIndex()
            uiState.isBookmarksOpen -> viewModel.closeBookmarks()
            uiState.isOverviewMode -> viewModel.setOverviewMode(false)
        }
    }

    val currentSurah = QuranData.getSurahForPage(uiState.currentPageNumber)
    val currentJuzNum = QuranData.getJuzNumberForPage(uiState.currentPageNumber)
    val currentJuzName = QuranData.getJuzName(currentJuzNum)

    // Dynamic background color animation: Warm eye-comforting parchment matching target app
    val targetBgColor = if (uiState.isOverviewMode) {
        QuranDarkGreen
    } else {
        if (uiState.isNightMode) QuranNightBackground else QuranParchment
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 350),
        label = "bg_color"
    )

    QuranSideDrawer(
        drawerState = drawerState,
        currentPageNumber = uiState.currentPageNumber,
        isNightMode = uiState.isNightMode,
        isNightShiftActive = uiState.isNightShiftActive,
        isBookmarked = uiState.isBookmarked,
        isPlayingAudio = playerState.isPlaying,
        currentReciterName = playerState.currentReciter.name,
        dailyPagesRead = uiState.dailyProgress?.pagesReadCount ?: 0,
        dailyTargetGoal = uiState.dailyProgress?.targetPagesGoal ?: 10,
        bookmarksCount = uiState.bookmarks.size,
        favoriteAyahsCount = uiState.favoriteAyahs.size,
        khatmahPlan = uiState.khatmahPlan,
        onOpenIndex = { viewModel.openIndex() },
        onOpenSearch = { viewModel.openSearch() },
        onOpenTafsir = { viewModel.openCurrentPageTafsir() },
        onOpenKhatmah = { viewModel.openKhatmahDialog() },
        onOpenDailyProgress = { viewModel.openDailyProgress() },
        onOpenBookmarks = { viewModel.openBookmarks() },
        onOpenGoToPage = { viewModel.openGoToPage() },
        onToggleAudio = { viewModel.playCurrentAyah() },
        onOpenReciterDialog = { viewModel.openReciterDialog() },
        onOpenPlaylists = { viewModel.openPlaylists() },
        onToggleBookmark = { viewModel.toggleBookmark() },
        onToggleNightMode = { viewModel.toggleNightMode() },
        onOpenNightShift = { viewModel.openNightShiftDialog() }
    ) {
        Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(uiState.isOverviewMode) {
                if (uiState.isOverviewMode) {
                    detectTapGestures {
                        viewModel.setOverviewMode(false)
                    }
                }
            }
            .testTag("quran_main_screen")
    ) {
        // Quran Pages Horizontal Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("quran_horizontal_pager"),
            contentPadding = if (uiState.isOverviewMode) {
                PaddingValues(horizontal = 38.dp, vertical = 72.dp)
            } else if (playerState.isPlayerVisible) {
                PaddingValues(bottom = 54.dp)
            } else {
                PaddingValues(0.dp)
            },
            pageSpacing = if (uiState.isOverviewMode) 12.dp else 0.dp
        ) { pageIndex ->
            val pageNum = pageIndex + 1
            val pageData = QuranData.getPage(pageNum)
            val pageSurah = QuranData.getSurahForPage(pageNum)
            val selectedAyahParts = uiState.selectedAyahKey?.split(":")
            val selectedSurahId = selectedAyahParts?.getOrNull(0)?.toIntOrNull()
            val selectedAyahNum = selectedAyahParts?.getOrNull(1)?.toIntOrNull()
            val pageSelectedAyah = if (selectedSurahId == pageSurah.id) selectedAyahNum else null

            QuranPageCard(
                page = pageData,
                isOverviewMode = uiState.isOverviewMode,
                isZoomedMode = uiState.isZoomedMode,
                isNightMode = uiState.isNightMode,
                selectedAyahNumber = pageSelectedAyah,
                onAyahClick = { surahId, ayahNum ->
                    viewModel.toggleAyahSelection(surahId, ayahNum)
                },
                onAyahLongPress = { surahId, ayahNum ->
                    // Long press on ayah: show Tafsir in popup dialog
                    viewModel.openTafsirForAyah(surahId, ayahNum)
                },
                onDoubleTap = {
                    // Double tap: toggle zoom mode (تكبير الكتابة بالضغط مرتين)
                    viewModel.toggleZoomMode()
                },
                onTopHeaderTap = {
                    // Single tap at top of screen: toggle overview mode (surahs carousel & bottom scrubber navigation)
                    viewModel.toggleOverviewMode()
                },
                onExitOverview = {
                    // Tap on screen: immediately exit overview mode and return to reading
                    viewModel.setOverviewMode(false)
                }
            )
        }

        // Night Shift Eye Comfort Warmth Overlay (Protects eyes during late-night reading sessions)
        if (uiState.isNightShiftActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF9E1B).copy(alpha = uiState.nightShiftWarmth))
            )
        }

        // Small Page Number at the bottom of the page
        AnimatedVisibility(
            visible = !uiState.isOverviewMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (playerState.isPlayerVisible) 54.dp else 6.dp)
        ) {
            Text(
                text = QuranData.toArabicDigits(uiState.currentPageNumber),
                fontFamily = AmiriFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = if (uiState.isNightMode) Color(0xFFB0A48E) else Color(0xFF6B6355),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .clickable { viewModel.openGoToPage() }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("small_bottom_page_number")
            )
        }

        // Minimal, Clean Top End Button to open the Quran Side Drawer (no bounding box)
        AnimatedVisibility(
            visible = !uiState.isOverviewMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 10.dp)
        ) {
            IconButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("open_side_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "فتح القائمة الجانبية",
                    tint = if (uiState.isNightMode) Color(0xFFB0A48E) else Color(0xFF6B6355),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Minimal, Clean Top Start Surah Name & Juz (no enclosing rectangle, smaller elegant font)
        AnimatedVisibility(
            visible = !uiState.isOverviewMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 10.dp, start = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { scope.launch { drawerState.open() } }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .testTag("top_surah_title_pill")
            ) {
                Text(
                    text = "${currentSurah.fullName} • ${currentJuzName}",
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = if (uiState.isNightMode) Color(0xFFB0A48E) else Color(0xFF6B6355)
                )
            }
        }

        // Top Surah Carousel (Visible only when zoomed out / overview mode is active)
        TopSurahCarousel(
            visible = uiState.isOverviewMode,
            currentSurah = currentSurah,
            currentPage = uiState.currentPageNumber,
            isNightMode = uiState.isNightMode,
            isAudioPlaying = playerState.isPlaying,
            onSurahSelected = { surah ->
                viewModel.selectSurah(surah)
                scope.launch {
                    pagerState.animateScrollToPage(surah.startPage - 1)
                }
            },
            onToggleNightMode = { viewModel.toggleNightMode() },
            onOpenGoToPage = { viewModel.openGoToPage() },
            onDailyProgressClick = { viewModel.openDailyProgress() },
            onAudioClick = { viewModel.playCurrentAyah() },
            onOpenIndex = { viewModel.openIndex() },
            onOpenSearch = { viewModel.openSearch() },
            onBookmarksClick = { viewModel.openBookmarks() },
            onKhatmahClick = { viewModel.openKhatmahDialog() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Bottom Scrubber & Controls (Visible only when zoomed out / overview mode is active)
        BottomScrubber(
            visible = uiState.isOverviewMode,
            currentPage = uiState.currentPageNumber,
            totalPages = totalPages,
            juzName = currentJuzName,
            juzNumber = currentJuzNum,
            isNightMode = uiState.isNightMode,
            isBookmarked = uiState.isBookmarked,
            onToggleNightMode = { viewModel.toggleNightMode() },
            onToggleBookmark = { viewModel.toggleBookmark() },
            onOpenGoToPage = { viewModel.openGoToPage() },
            onPageSelected = { newPage ->
                viewModel.onPageChanged(newPage)
                scope.launch {
                    pagerState.animateScrollToPage(newPage - 1)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Surah Index Sheet (Fihris with Surahs, Pages, and Bookmarks)
        SurahIndexSheet(
            isOpen = uiState.isIndexOpen,
            onDismiss = { viewModel.closeIndex() },
            onSurahClick = { surah ->
                viewModel.selectSurah(surah)
                scope.launch {
                    pagerState.animateScrollToPage(surah.startPage - 1)
                }
            },
            onPageClick = { targetPage ->
                viewModel.onPageChanged(targetPage)
                scope.launch {
                    pagerState.animateScrollToPage(targetPage - 1)
                }
            },
            bookmarks = uiState.bookmarks,
            onDeleteBookmark = { page ->
                viewModel.deleteBookmark(page)
            }
        )

        // Saved Bookmarks & Favorites Sheet
        BookmarksSheet(
            isOpen = uiState.isBookmarksOpen,
            bookmarks = uiState.bookmarks,
            favoriteAyahs = uiState.favoriteAyahs,
            onDismiss = { viewModel.closeBookmarks() },
            onBookmarkClick = { page ->
                viewModel.onPageChanged(page)
            },
            onDeleteBookmark = { page ->
                viewModel.deleteBookmark(page)
            },
            onFavoriteAyahClick = { fav ->
                viewModel.navigateToFavoriteAyah(fav)
            },
            onPlayFavoriteAyah = { fav ->
                viewModel.playFavoriteAyah(fav)
            },
            onDeleteFavoriteAyah = { id ->
                viewModel.deleteFavoriteAyah(id)
            }
        )

        // Go to Page Dialog
        GoToPageDialog(
            currentPage = uiState.currentPageNumber,
            isOpen = uiState.isGoToPageOpen,
            onDismiss = { viewModel.closeGoToPage() },
            onPageSelected = { targetPage ->
                viewModel.onPageChanged(targetPage)
                scope.launch {
                    pagerState.animateScrollToPage(targetPage - 1)
                }
            }
        )

        // Bottom Audio Player Bar (Compact, slim dock at bottom without covering text)
        AudioPlayerBar(
            playerState = playerState,
            isNightMode = uiState.isNightMode,
            onTogglePlayPause = { viewModel.toggleAudioPlayPause() },
            onPreviousAyah = { viewModel.playPreviousAyah() },
            onNextAyah = { viewModel.playNextAyah() },
            onOpenReciterSelection = { viewModel.openReciterDialog() },
            onOpenPlaylists = { viewModel.openPlaylists() },
            onAddToPlaylist = { viewModel.openAddToPlaylist() },
            onClosePlayer = { viewModel.closeAudioPlayer() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Floating Ayah Action Bar (Tafsir, Audio, Favorite, Copy) when an ayah is selected
        val selectedAyahParts = uiState.selectedAyahKey?.split(":")
        val selSurahId = selectedAyahParts?.getOrNull(0)?.toIntOrNull()
        val selAyahNum = selectedAyahParts?.getOrNull(1)?.toIntOrNull()
        val isAyahFav = if (selSurahId != null && selAyahNum != null) {
            viewModel.isAyahFavorite(selSurahId, selAyahNum)
        } else false

        AyahActionBar(
            visible = uiState.selectedAyahKey != null && !uiState.isOverviewMode,
            selectedAyahKey = uiState.selectedAyahKey,
            isNightMode = uiState.isNightMode,
            isFavorite = isAyahFav,
            onOpenTafsir = { surahId, ayahNum ->
                viewModel.openTafsirForAyah(surahId, ayahNum)
            },
            onPlayAyah = { surahId, ayahNum ->
                viewModel.playAyahDirectly(surahId, ayahNum)
            },
            onToggleFavorite = { surahId, ayahNum ->
                viewModel.toggleFavoriteAyah(surahId, ayahNum)
            },
            onDismiss = {
                viewModel.clearAyahSelection()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (playerState.isPlayerVisible) 60.dp else 12.dp)
        )

        // Reciter Selection Dialog
        ReciterSelectionDialog(
            isOpen = uiState.isReciterDialogOpen,
            selectedReciter = playerState.currentReciter,
            onReciterSelected = { reciter ->
                viewModel.setReciter(reciter)
            },
            onDismiss = { viewModel.closeReciterDialog() }
        )

        // Tafsir Bottom Sheet Overlay (shown on long-pressing an ayah or from drawer or action bar)
        val isCurrentAyahFav = uiState.currentTafsir?.let {
            viewModel.isAyahFavorite(it.surahId, it.ayahNumber)
        } ?: false

        TafsirBottomSheet(
            tafsirResult = uiState.currentTafsir,
            isOpen = uiState.isTafsirOpen,
            isNightMode = uiState.isNightMode,
            isFavorite = isCurrentAyahFav,
            onToggleFavorite = {
                uiState.currentTafsir?.let {
                    viewModel.toggleFavoriteAyah(it.surahId, it.ayahNumber)
                }
            },
            onPlayAudio = { surahId, ayahNum ->
                viewModel.closeTafsir()
                viewModel.playAyahDirectly(surahId, ayahNum)
            },
            onNextAyah = { viewModel.nextTafsirAyah() },
            onPreviousAyah = { viewModel.previousTafsirAyah() },
            onDismiss = { viewModel.closeTafsir() }
        )

        // Daily Progress Dialog (Daily Wird Tracker & Visual Indicator)
        DailyProgressDialog(
            isOpen = uiState.isDailyProgressOpen,
            dailyProgress = uiState.dailyProgress,
            streakDays = uiState.streakDays,
            onGoalChanged = { newGoal ->
                viewModel.updateDailyGoal(newGoal)
            },
            onDismiss = { viewModel.closeDailyProgress() }
        )

        // Night Shift Eye Comfort Dialog
        NightShiftDialog(
            isOpen = uiState.isNightShiftDialogOpen,
            isNightShiftActive = uiState.isNightShiftActive,
            isAutoEnabled = uiState.nightShiftAutoEnabled,
            isManualEnabled = uiState.nightShiftManualEnabled,
            warmthLevel = uiState.nightShiftWarmth,
            onToggleAuto = { viewModel.toggleNightShiftAuto(it) },
            onToggleManual = { viewModel.toggleNightShiftManual(it) },
            onWarmthChanged = { viewModel.updateNightShiftWarmth(it) },
            onDismiss = { viewModel.closeNightShiftDialog() }
        )

        // Playlists Management Sheet
        PlaylistsSheet(
            isOpen = uiState.isPlaylistsOpen,
            playlists = uiState.playlists,
            selectedPlaylist = uiState.selectedPlaylist,
            playlistItems = uiState.selectedPlaylistItems,
            onSelectPlaylist = { pl -> viewModel.selectPlaylist(pl) },
            onBackToPlaylists = { viewModel.backToPlaylists() },
            onCreatePlaylist = { name, desc -> viewModel.createPlaylist(name, desc) },
            onDeletePlaylist = { id -> viewModel.deletePlaylist(id) },
            onPlayItem = { item -> viewModel.playPlaylistItem(item) },
            onDeleteItem = { id -> viewModel.deletePlaylistItem(id) },
            onDismiss = { viewModel.closePlaylists() }
        )

        // Add to Playlist Dialog
        val currentAudioSurah = QuranData.surahs.find { it.id == playerState.currentSurah }
        val currentAudioSurahName = currentAudioSurah?.fullName ?: "سورة"
        AddToPlaylistDialog(
            isOpen = uiState.isAddToPlaylistOpen,
            surahId = playerState.currentSurah,
            surahName = currentAudioSurahName,
            ayahNumber = playerState.currentAyah,
            reciter = playerState.currentReciter,
            playlists = uiState.playlists,
            onAddToPlaylist = { plId -> viewModel.addCurrentRecitationToPlaylist(plId) },
            onCreateAndAdd = { name -> viewModel.createPlaylistAndAddCurrent(name) },
            onDismiss = { viewModel.closeAddToPlaylist() }
        )

        // Quran Search Sheet (Search Surahs & Ayahs by name, number, or keyword)
        QuranSearchSheet(
            isOpen = uiState.isSearchOpen,
            onDismiss = { viewModel.closeSearch() },
            onSurahClick = { surah ->
                viewModel.selectSurah(surah)
                viewModel.closeSearch()
                scope.launch {
                    pagerState.animateScrollToPage(surah.startPage - 1)
                }
            },
            onAyahClick = { surahId, ayahNum, pageNum ->
                viewModel.onPageChanged(pageNum)
                viewModel.toggleAyahSelection(surahId, ayahNum)
                viewModel.closeSearch()
                scope.launch {
                    pagerState.animateScrollToPage(pageNum - 1)
                }
            },
            onPlayAyah = { surahId, ayahNum ->
                viewModel.playAyahDirectly(surahId, ayahNum)
            }
        )

        // Khatmah Plan Dialog (Manage duration, daily target, reminder)
        KhatmahDialog(
            isOpen = uiState.isKhatmahDialogOpen,
            khatmahPlan = uiState.khatmahPlan,
            todayPagesRead = uiState.dailyProgress?.pagesReadCount ?: 0,
            currentPageNumber = uiState.currentPageNumber,
            onDismiss = { viewModel.closeKhatmahDialog() },
            onStartOrUpdateKhatmah = { days, hour, min, reminder ->
                viewModel.startOrUpdateKhatmah(days, hour, min, reminder)
            },
            onStartTodayWird = { startPage ->
                viewModel.onPageChanged(startPage)
                scope.launch {
                    pagerState.animateScrollToPage(startPage - 1)
                }
            },
            onResetKhatmah = { viewModel.resetKhatmah() }
        )
    }
}
}
