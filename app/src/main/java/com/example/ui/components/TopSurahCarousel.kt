package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.data.SurahInfo
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranDarkGreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldLight

@Composable
fun TopSurahCarousel(
    visible: Boolean,
    currentSurah: SurahInfo,
    currentPage: Int = 1,
    isNightMode: Boolean = false,
    isAudioPlaying: Boolean = false,
    onSurahSelected: (SurahInfo) -> Unit,
    onToggleNightMode: () -> Unit = {},
    onOpenGoToPage: () -> Unit = {},
    onDailyProgressClick: () -> Unit = {},
    onAudioClick: () -> Unit = {},
    onOpenIndex: () -> Unit,
    onOpenSearch: () -> Unit,
    onBookmarksClick: () -> Unit,
    onKhatmahClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        val listState = rememberLazyListState()

        // Auto-scroll to center active surah when currentSurah changes
        LaunchedEffect(currentSurah.id) {
            val targetIdx = (currentSurah.id - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIdx)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Action Icons Row: Night Mode, Go to Page, Bookmarks, Search, and Menu (Index)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Actions: Quick Mode Toggle & Page Jump
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Day / Night Toggle button
                    IconButton(
                        onClick = onToggleNightMode,
                        modifier = Modifier.testTag("top_night_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isNightMode) "الوضع الفاتح" else "الوضع الليلي (المظلم)",
                            tint = if (isNightMode) QuranGoldLight else Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Direct Page Jump Button
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenGoToPage)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("top_page_number_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Numbers,
                                contentDescription = "الانتقال لصفحة",
                                tint = QuranGoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ص ${QuranData.toArabicDigits(currentPage)}",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Daily Progress Button in Carousel
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onDailyProgressClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("top_daily_progress_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "الورد اليومي",
                                tint = QuranGoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "الورد",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Khatmah Plan Tracker Button in Carousel
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE67E22).copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFFB74D).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onKhatmahClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("top_khatmah_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "ختمة القرآن",
                                tint = QuranGoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "الختمة",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Right Actions: Audio, Search, Bookmarks, Index Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onAudioClick,
                        modifier = Modifier.testTag("top_audio_recitation_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "الاستماع للتلاوة",
                            tint = if (isAudioPlaying) QuranGoldLight else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenSearch,
                        modifier = Modifier.testTag("top_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onBookmarksClick,
                        modifier = Modifier.testTag("top_bookmarks_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "العلامات المرجعية",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenIndex,
                        modifier = Modifier.testTag("open_index_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "فهرس السور",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Surah Carousel Row: Allows swiping and tapping through Surahs from top
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("surah_carousel"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(QuranData.surahs) { _, surah ->
                    val isSelected = surah.id == currentSurah.id

                    if (isSelected) {
                        MihrabArchHeader(
                            surahName = surah.fullName,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clickable { onSurahSelected(surah) }
                                .testTag("active_surah_${surah.id}")
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .clickable { onSurahSelected(surah) }
                                .testTag("surah_item_${surah.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = surah.fullName,
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 17.sp,
                                color = Color.White.copy(alpha = 0.72f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
