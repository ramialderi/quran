package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.QuranData
import com.example.data.QuranPage
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranNightBackground
import com.example.ui.theme.QuranParchment

@Composable
fun QuranPageCard(
    page: QuranPage,
    isOverviewMode: Boolean,
    isZoomedMode: Boolean,
    isNightMode: Boolean,
    selectedAyahNumber: Int? = null,
    onAyahClick: (surahId: Int, ayahNumber: Int) -> Unit = { _, _ -> },
    onAyahLongPress: (surahId: Int, ayahNumber: Int) -> Unit = { _, _ -> },
    onDoubleTap: () -> Unit,
    onTopHeaderTap: () -> Unit,
    onExitOverview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageUrl = remember(page.pageNumber) {
        QuranData.getPageImageUrl(page.pageNumber)
    }

    val targetScale = if (isOverviewMode) {
        0.82f
    } else if (isZoomedMode) {
        1.035f // Safe enlargement: fills view comfortably while keeping 100% of letters inside the screen
    } else {
        1.0f
    }

    val scaleAnim by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 280),
        label = "page_scale"
    )

    // Night Mode color matrix (dark soothing background with light text)
    val nightModeMatrix = remember {
        ColorMatrix(
            floatArrayOf(
                -0.80f,     0f,     0f, 0f, 230f,
                    0f, -0.75f,     0f, 0f, 215f,
                    0f,     0f, -0.65f, 0f, 180f,
                    0f,     0f,     0f, 1f,   0f
            )
        )
    }

    // Warm, eye-comforting parchment matrix matching the left phone in the user photo:
    // Darker warm parchment background + bolder, high-contrast, crystal-clear black calligraphy
    val warmParchmentMatrix = remember {
        ColorMatrix(
            floatArrayOf(
                1.069f,     0f,     0f, 0f, -35.3f,
                    0f, 1.024f,     0f, 0f, -33.8f,
                    0f,     0f, 0.938f, 0f, -31.0f,
                    0f,     0f,     0f, 1f,     0f
            )
        )
    }

    val cardBg = if (isNightMode) QuranNightBackground else QuranParchment
    val surahInfo = remember(page.pageNumber) { QuranData.getSurahForPage(page.pageNumber) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .scale(scaleAnim)
            .background(cardBg)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .pointerInput(page.pageNumber, page.verses, isOverviewMode) {
                detectTapGestures(
                    onDoubleTap = {
                        // Double tap: toggle zoom/enlargement (تكبير الكتابة بالضغط مرتين)
                        onDoubleTap()
                    },
                    onLongPress = { offset ->
                        // Long press on ayah: open Tafsir popup
                        if (!isOverviewMode && page.verses.isNotEmpty()) {
                            val topMargin = size.height * 0.08f
                            val contentHeight = size.height * 0.84f
                            val relY = (offset.y - topMargin) / contentHeight
                            if (relY in 0f..1f) {
                                val index = (relY * page.verses.size).toInt().coerceIn(0, page.verses.size - 1)
                                val tappedVerse = page.verses[index]
                                onAyahLongPress(surahInfo.id, tappedVerse.ayahNumber)
                            }
                        }
                    },
                    onTap = { offset ->
                        if (isOverviewMode) {
                            // When in navigation/overview mode, tapping anywhere on the screen exits it immediately!
                            onExitOverview()
                        } else if (offset.y < size.height * 0.12f) {
                            // Single tap at top of screen: toggle overview mode (surahs carousel & bottom scrubber navigation)
                            onTopHeaderTap()
                        } else if (page.verses.isNotEmpty()) {
                            // Single tap on page: shade/highlight the ayah at this position (pure shading, no text/labels)
                            val topMargin = size.height * 0.08f
                            val contentHeight = size.height * 0.84f
                            val relY = (offset.y - topMargin) / contentHeight
                            if (relY in 0f..1f) {
                                val index = (relY * page.verses.size).toInt().coerceIn(0, page.verses.size - 1)
                                val tappedVerse = page.verses[index]
                                onAyahClick(surahInfo.id, tappedVerse.ayahNumber)
                            }
                        }
                    }
                )
            }
            .testTag("quran_page_card_${page.pageNumber}"),
        contentAlignment = Alignment.Center
    ) {
        // Authentic Madani Quran Page Image with warm parchment filter & bolder clear calligraphy
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "صفحة رقم ${QuranData.toArabicDigits(page.pageNumber)}",
            contentScale = ContentScale.Fit,
            colorFilter = if (isNightMode) {
                ColorFilter.colorMatrix(nightModeMatrix)
            } else {
                ColorFilter.colorMatrix(warmParchmentMatrix)
            },
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = QuranGold,
                        strokeWidth = 2.5.dp
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Pure Ayah Shading (تظليل الآية فقط بدون أي كتابة أو نصوص)
        if (selectedAyahNumber != null && page.verses.isNotEmpty()) {
            val selectedIndex = page.verses.indexOfFirst { it.ayahNumber == selectedAyahNumber }
            if (selectedIndex >= 0) {
                val topMargin = maxHeight * 0.08f
                val contentHeight = maxHeight * 0.84f
                val verseHeight = (contentHeight / page.verses.size).coerceAtLeast(24.dp)
                val highlightTop = topMargin + (contentHeight / page.verses.size) * selectedIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(verseHeight)
                        .align(Alignment.TopCenter)
                        .offset(y = highlightTop)
                        .background(
                            color = Color(0x33D4AF37), // Pure soft translucent Islamic golden shading
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0x66D4AF37),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .testTag("ayah_highlight_${selectedAyahNumber}")
                )
            }
        }

        // Top clickable hotspot covering the Juz & Surah header to toggle navigation
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    if (isOverviewMode) onExitOverview() else onTopHeaderTap()
                }
                .testTag("top_header_hotspot_${page.pageNumber}")
        )
    }
}
