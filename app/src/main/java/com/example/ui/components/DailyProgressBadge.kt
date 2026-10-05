package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyReadingProgress
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranTextDark

@Composable
fun DailyProgressBadge(
    dailyProgress: DailyReadingProgress?,
    isNightMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = dailyProgress ?: DailyReadingProgress(date = "")
    val pagesRead = progress.pagesReadCount
    val targetGoal = progress.targetPagesGoal.coerceAtLeast(1)
    val fraction = (pagesRead.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)
    val isCompleted = pagesRead >= targetGoal

    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 500),
        label = "badge_fraction"
    )

    Box(
        modifier = modifier
            .background(
                color = if (isCompleted) {
                    Color(0xFF2E7D32).copy(alpha = 0.2f)
                } else {
                    if (isNightMode) Color(0x332B3226) else Color(0x33EDE5D6)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isCompleted) Color(0xFF2E7D32) else QuranGold.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag("daily_progress_badge"),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mini circular progress ring
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(24.dp),
                    color = Color.LightGray.copy(alpha = 0.4f),
                    strokeWidth = 2.5.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )

                CircularProgressIndicator(
                    progress = { animatedFraction },
                    modifier = Modifier.size(24.dp),
                    color = if (isCompleted) Color(0xFF2E7D32) else QuranGoldBanner,
                    strokeWidth = 2.5.dp,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "تم الهدف",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "${QuranData.toArabicDigits(pagesRead)}/${QuranData.toArabicDigits(targetGoal)}",
                fontFamily = AmiriFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = if (isCompleted) {
                    Color(0xFF2E7D32)
                } else if (isNightMode) {
                    Color(0xFFC7AF80)
                } else {
                    QuranTextDark
                }
            )
        }
    }
}
