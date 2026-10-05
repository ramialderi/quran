package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldLight

@Composable
fun BottomScrubber(
    visible: Boolean,
    currentPage: Int,
    totalPages: Int = 604,
    juzName: String,
    juzNumber: Int,
    isNightMode: Boolean,
    isBookmarked: Boolean,
    onToggleNightMode: () -> Unit,
    onToggleBookmark: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onOpenGoToPage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // First Row: Night mode toggle, Juz Name, Bookmark toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Night mode toggle (Crescent Moon icon as shown on the left in Screenshot 1)
                IconButton(
                    onClick = onToggleNightMode,
                    modifier = Modifier.testTag("night_mode_toggle")
                ) {
                    Icon(
                        imageVector = if (isNightMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                        contentDescription = "تبديل الوضع الليلي",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Juz Title centered (e.g., "الجُزْءُ الرَّابِعُ")
                Text(
                    text = juzName,
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("bottom_juz_title")
                )

                // Bookmark toggle on the right (as shown on the right in Screenshot 1)
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bookmark_toggle")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "إشارة مرجعية",
                        tint = if (isBookmarked) QuranGold else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Diamond page scrubber row (Rotated squares track matching Screenshot 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 5 page diamonds showing exact page numbers around current page
                val activeDiamondIndex = 2

                for (i in 0..4) {
                    val isSelected = i == activeDiamondIndex
                    val offset = i - activeDiamondIndex
                    val targetPage = (currentPage + offset).coerceIn(1, totalPages)

                    DiamondScrubberItem(
                        isSelected = isSelected,
                        label = QuranData.toArabicDigits(targetPage),
                        onClick = { onPageSelected(targetPage) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth Scrubbing Track / Slider to navigate pages via dragging
            Slider(
                value = currentPage.toFloat(),
                onValueChange = { newValue ->
                    onPageSelected(newValue.toInt().coerceIn(1, totalPages))
                },
                valueRange = 1f..totalPages.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .testTag("page_scrubber_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = QuranGold,
                    activeTrackColor = QuranGoldLight,
                    inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                )
            )

            // Page numbers display row (start, current page, end)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "صفحة ${QuranData.toArabicDigits(1)}",
                    fontFamily = AmiriFamily,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenGoToPage)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                        .testTag("scrubber_page_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "صفحة ${QuranData.toArabicDigits(currentPage)} من ${QuranData.toArabicDigits(totalPages)} ▾",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = QuranGoldLight
                    )
                }

                Text(
                    text = "صفحة ${QuranData.toArabicDigits(totalPages)}",
                    fontFamily = AmiriFamily,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
