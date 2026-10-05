package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KhatmahPlan
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranDarkGreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranTextDark
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KhatmahDialog(
    isOpen: Boolean,
    khatmahPlan: KhatmahPlan?,
    todayPagesRead: Int,
    currentPageNumber: Int,
    onDismiss: () -> Unit,
    onStartOrUpdateKhatmah: (durationDays: Int, reminderHour: Int, reminderMinute: Int, reminderEnabled: Boolean) -> Unit,
    onStartTodayWird: (startPage: Int) -> Unit,
    onResetKhatmah: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isOpen) return

    val scrollState = rememberScrollState()
    var isEditing by remember { mutableStateOf(khatmahPlan == null) }

    // Form states
    var selectedDurationDays by remember(khatmahPlan) {
        mutableIntStateOf(khatmahPlan?.durationDays ?: 30)
    }
    var isReminderEnabled by remember(khatmahPlan) {
        mutableStateOf(khatmahPlan?.isReminderEnabled ?: true)
    }
    var reminderHour by remember(khatmahPlan) {
        mutableIntStateOf(khatmahPlan?.dailyReminderHour ?: 20)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuranIndexBackground,
        dragHandle = null,
        modifier = Modifier.testTag("khatmah_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(QuranIndexBackground)
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(QuranDarkGreen, Color(0xFF1B4D3E), QuranDarkGreen)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(QuranGold.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = QuranGoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ختمة القرآن الكريم",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "متابعة وتحديد مدة الختمة وحساب الورد اليومي",
                            fontFamily = AmiriFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFC7D7D0)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("close_khatmah_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (khatmahPlan != null && !isEditing) {
                    // Active Khatmah Dashboard View
                    val totalPages = 604
                    val currentProgressPage = khatmahPlan.currentProgressPage.coerceIn(1, totalPages)
                    val durationDays = khatmahPlan.durationDays
                    val elapsedMillis = (System.currentTimeMillis() - khatmahPlan.startDateTimestamp).coerceAtLeast(0)
                    val elapsedDays = ((elapsedMillis / (1000L * 60 * 60 * 24)).toInt() + 1).coerceIn(1, durationDays)
                    val daysRemaining = (durationDays - elapsedDays + 1).coerceAtLeast(1)
                    val pagesRemaining = (totalPages - currentProgressPage).coerceAtLeast(0)

                    val dailyRequiredPages = ceil(totalPages.toDouble() / durationDays).toInt()
                    val completionPercent = (currentProgressPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)

                    val pagesLeftToday = (dailyRequiredPages - todayPagesRead).coerceAtLeast(0)
                    val isTodayDone = pagesLeftToday == 0

                    // Expected completion date
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = khatmahPlan.startDateTimestamp
                    cal.add(Calendar.DAY_OF_YEAR, durationDays)
                    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
                    val targetDateStr = sdf.format(cal.time)

                    // Hero Progress Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("khatmah_dashboard_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Circular Ring + Progress Numbers
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFFF0EAE1),
                                    strokeWidth = 9.dp
                                )
                                CircularProgressIndicator(
                                    progress = { completionPercent },
                                    modifier = Modifier.fillMaxSize(),
                                    color = QuranGoldBanner,
                                    strokeWidth = 9.dp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${QuranData.toArabicDigits((completionPercent * 100).toInt())}%",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = QuranTextDark
                                    )
                                    Text(
                                        text = "المُنجز",
                                        fontFamily = AmiriFamily,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Linear Progress Bar
                            LinearProgressIndicator(
                                progress = { completionPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = QuranGoldBanner,
                                trackColor = Color(0xFFF0EAE1)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3 Key Stats: Current Page, Days Remaining, Daily Pages
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${QuranData.toArabicDigits(currentProgressPage)} / ٦٠٤",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = QuranTextDark
                                    )
                                    Text(
                                        text = "الصفحات المنجزة",
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(32.dp)
                                        .background(Color(0xFFE2D6C0))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${QuranData.toArabicDigits(elapsedDays)} / ${QuranData.toArabicDigits(durationDays)}",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = QuranDarkGreen
                                    )
                                    Text(
                                        text = "اليوم الحالي",
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(32.dp)
                                        .background(Color(0xFFE2D6C0))
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${QuranData.toArabicDigits(dailyRequiredPages)} صفحة",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = QuranGoldBanner
                                    )
                                    Text(
                                        text = "الورد اليومي",
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    // Today's Wird Status Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("khatmah_today_wird_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTodayDone) Color(0xFFE8F5E9) else Color(0xFFFFF9EE)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isTodayDone) Color(0xFFA5D6A7) else Color(0xFFFFE082)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isTodayDone) Icons.Default.CheckCircle else Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = if (isTodayDone) Color(0xFF2E7D32) else Color(0xFFE67E22),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isTodayDone) "تم إنجاز ورد اليوم مباركاً!" else "ورد اليوم المطلوب إتمامه:",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isTodayDone) Color(0xFF1B5E20) else QuranTextDark
                                    )
                                }

                                Text(
                                    text = "${QuranData.toArabicDigits(todayPagesRead)} / ${QuranData.toArabicDigits(dailyRequiredPages)} ص",
                                    fontFamily = AmiriFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isTodayDone) Color(0xFF2E7D32) else Color(0xFFE67E22)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isTodayDone) {
                                    "تقبل الله طاعتكم! لقد قرأت اليوم $todayPagesRead صفحة وأتممت نصيبك المحدد لختم القرآن في موعدك."
                                } else {
                                    "لقد قرأت اليوم $todayPagesRead صفحة، ويتبقى لك $pagesLeftToday صفحة لإتمام الورد اليومي (صفحة $currentProgressPage إلى صفحة ${(currentProgressPage + pagesLeftToday).coerceAtMost(604)})."
                                },
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                color = QuranTextDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Start Today's Wird Button
                            Button(
                                onClick = {
                                    onStartTodayWird(currentProgressPage)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTodayDone) Color(0xFF2E7D32) else QuranGoldBanner
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_today_wird_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTodayDone) "متابعة التلاوة من صفحة ${QuranData.toArabicDigits(currentProgressPage)}" else "ابدأ قراءة ورد اليوم (صفحة ${QuranData.toArabicDigits(currentProgressPage)})",
                                    fontFamily = AmiriFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Daily Reminder Status Info
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = QuranGoldBanner,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "تنبيه الورد اليومي",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = QuranTextDark
                                    )
                                    Text(
                                        text = if (khatmahPlan.isReminderEnabled) {
                                            "يتم التنبيه يومياً عند الساعة ${formatHour(khatmahPlan.dailyReminderHour)}"
                                        } else {
                                            "التنبيه معطل حالياً"
                                        },
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "الختم: $targetDateStr",
                                    fontFamily = AmiriFamily,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Edit & Reset Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isEditing = true },
                            border = BorderStroke(1.dp, QuranGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_khatmah_plan_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = QuranGoldBanner,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تعديل الخطة",
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                color = QuranTextDark
                            )
                        }

                        OutlinedButton(
                            onClick = { onResetKhatmah() },
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reset_khatmah_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بدء ختمة جديدة",
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                color = Color.Red.copy(alpha = 0.8f)
                            )
                        }
                    }
                } else {
                    // Create / Edit Khatmah Plan View
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFE67E22),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "حدد المدة الزمنية المرغوبة لختم القرآن:",
                                    fontFamily = AmiriFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = QuranTextDark
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Preset Duration Chips
                            val presets = listOf(
                                7 to "٧ أيام (أسبوع)",
                                10 to "١٠ أيام (العشر)",
                                15 to "١٥ يوماً (نصف شهر)",
                                30 to "٣٠ يوماً (شهر كامل)",
                                60 to "٦٠ يوماً (شهران)",
                                90 to "٩٠ يوماً (٣ أشهر)"
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                presets.forEach { (days, label) ->
                                    val isSelected = selectedDurationDays == days
                                    val pagesPerDay = ceil(604.0 / days).toInt()
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedDurationDays = days },
                                        label = {
                                            Text(
                                                text = "$label • $pagesPerDay ص/يوم",
                                                fontFamily = AmiriFamily,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = QuranGoldBanner,
                                            selectedLabelColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("khatmah_preset_${days}_days")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFF0EAE1))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Calculation summary box
                            val calculatedDailyPages = ceil(604.0 / selectedDurationDays).toInt()
                            val partsDesc = if (calculatedDailyPages >= 20) {
                                "ما يعادل ${calculatedDailyPages / 20} جزءاً تقريباً كل يوم"
                            } else {
                                "ما يعادل ${(calculatedDailyPages * 2) / 20.0} حزباً يومياً"
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFF9EE), RoundedCornerShape(12.dp))
                                    .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "📊 الحساب التلقائي للورد اليومي:",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = QuranGoldBanner
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• عدد الصفحات اليومي المطلوب: ${QuranData.toArabicDigits(calculatedDailyPages)} صفحة يومياً.\n• التقدير: $partsDesc.\n• إجمالي صفحات المصحف الشريف: ٦٠٤ صفحة.",
                                        fontFamily = AmiriFamily,
                                        fontSize = 13.sp,
                                        lineHeight = 22.sp,
                                        color = QuranTextDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFF0EAE1))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Reminder configuration
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = QuranGoldBanner,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "تنبيه إتمام الورد اليومي",
                                            fontFamily = AmiriFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = QuranTextDark
                                        )
                                        Text(
                                            text = "تذكير يومي لإتمام وردك وختم القرآن في موعدك",
                                            fontFamily = AmiriFamily,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Switch(
                                    checked = isReminderEnabled,
                                    onCheckedChange = { isReminderEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = QuranGoldBanner
                                    ),
                                    modifier = Modifier.testTag("khatmah_reminder_switch")
                                )
                            }

                            if (isReminderEnabled) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "اختر وقت التنبيه اليومي المفضل:",
                                    fontFamily = AmiriFamily,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val reminderTimes = listOf(
                                    5 to "الفجر (٥:٠٠ ص)",
                                    13 to "الظهر (١:٠٠ م)",
                                    16 to "العصر (٤:٠٠ م)",
                                    20 to "العشاء (٨:٠٠ م)",
                                    22 to "قبل النوم (١٠:٠٠ م)"
                                )

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    reminderTimes.forEach { (hour, label) ->
                                        FilterChip(
                                            selected = reminderHour == hour,
                                            onClick = { reminderHour = hour },
                                            label = {
                                                Text(
                                                    text = label,
                                                    fontFamily = AmiriFamily,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF2E7D32),
                                                selectedLabelColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Save Khatmah Plan Button
                    Button(
                        onClick = {
                            onStartOrUpdateKhatmah(
                                selectedDurationDays,
                                reminderHour,
                                0,
                                isReminderEnabled
                            )
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = QuranDarkGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_khatmah_plan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (khatmahPlan != null) "حفظ التعديلات في خطة الختمة" else "بدء خطة الختمة المباركة (${QuranData.toArabicDigits(selectedDurationDays)} يوماً)",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    if (khatmahPlan != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { isEditing = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "إلغاء والعودة للوحة الختمة",
                                fontFamily = AmiriFamily,
                                fontSize = 14.sp,
                                color = QuranTextDark
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatHour(hour: Int): String {
    return when {
        hour == 0 -> "١٢:٠٠ منتصف الليل"
        hour < 12 -> "${QuranData.toArabicDigits(hour)}:٠٠ صباحاً"
        hour == 12 -> "١٢:٠٠ ظهراً"
        else -> "${QuranData.toArabicDigits(hour - 12)}:٠٠ مساءً"
    }
}
