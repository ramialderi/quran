package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyReadingProgress
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranTextDark

@Composable
fun DailyProgressDialog(
    isOpen: Boolean,
    dailyProgress: DailyReadingProgress?,
    streakDays: Int = 1,
    onGoalChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val progress = dailyProgress ?: DailyReadingProgress(date = "")
    val pagesRead = progress.pagesReadCount
    val targetGoal = progress.targetPagesGoal.coerceAtLeast(1)
    val fraction = (pagesRead.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)
    val percentage = (fraction * 100).toInt()
    val isCompleted = pagesRead >= targetGoal

    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 600),
        label = "daily_fraction"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = Color(0xFFFBF6EE),
        shape = RoundedCornerShape(22.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_progress_dialog"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Title and Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(QuranGold.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = QuranGoldBanner,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "الورد القرآني اليومي",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = QuranTextDark
                            )
                            Text(
                                text = "متابعة إنجاز القراءة اليومية",
                                fontFamily = AmiriFamily,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_daily_progress_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Circular Progress Indicator & Completion Gauge
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .testTag("circular_progress_gauge"),
                    contentAlignment = Alignment.Center
                ) {
                    // Track background
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(140.dp),
                        color = Color(0xFFEADBCE),
                        strokeWidth = 10.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )

                    // Active progress
                    CircularProgressIndicator(
                        progress = { animatedFraction },
                        modifier = Modifier.size(140.dp),
                        color = if (isCompleted) Color(0xFF2E7D32) else QuranGoldBanner,
                        strokeWidth = 10.dp,
                        trackColor = Color.Transparent,
                        strokeCap = StrokeCap.Round
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "أنجزت الهدف",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Text(
                                text = "${QuranData.toArabicDigits(percentage)}%",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = QuranTextDark
                            )
                        }

                        Text(
                            text = "${QuranData.toArabicDigits(pagesRead)} / ${QuranData.toArabicDigits(targetGoal)} صفحة",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isCompleted) Color(0xFF2E7D32) else QuranGoldBanner
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Motivational Status Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFF3ECE0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isCompleted) Color(0xFF81C784) else Color(0xFFDFCFAF),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCompleted) {
                            "ما شاء الله! أتممت وردك اليومي، بارك الله فيك! 🌟"
                        } else if (pagesRead > 0) {
                            "تبقى لك ${QuranData.toArabicDigits(targetGoal - pagesRead)} صفحات لإتمام ورد اليوم"
                        } else {
                            "ابدأ بقراءة القرآن اليوم لتحقيق وردك اليومي المبارك"
                        },
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isCompleted) Color(0xFF1B5E20) else QuranTextDark,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reading Streak Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFDF0E2), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "تتابع القراءة",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "التتابع القرآني: ${QuranData.toArabicDigits(streakDays)} أيام متواصلة",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFBF360C)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Goal Selection Section
                Text(
                    text = "تحديد الهدف اليومي (عدد الصفحات):",
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val goalOptions = listOf(5, 10, 20, 40)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    goalOptions.forEach { goal ->
                        val isSelected = targetGoal == goal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) QuranGoldBanner else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) QuranGoldBanner else Color(0xFFDCCFBB),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onGoalChanged(goal) }
                                .padding(vertical = 8.dp)
                                .testTag("goal_option_$goal"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = QuranData.toArabicDigits(goal),
                                    fontFamily = AmiriFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isSelected) Color.White else QuranTextDark
                                )
                                Text(
                                    text = "صفحة",
                                    fontFamily = AmiriFamily,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
