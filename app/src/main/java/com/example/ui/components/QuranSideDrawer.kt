package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Badge
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KhatmahPlan
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.AmiriQuranFamily
import com.example.ui.theme.QuranDarkGreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranGoldLight
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranTextDark
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun QuranSideDrawer(
    drawerState: DrawerState,
    currentPageNumber: Int,
    isNightMode: Boolean,
    isNightShiftActive: Boolean,
    isBookmarked: Boolean,
    isPlayingAudio: Boolean,
    currentReciterName: String,
    dailyPagesRead: Int,
    dailyTargetGoal: Int,
    bookmarksCount: Int,
    favoriteAyahsCount: Int,
    khatmahPlan: KhatmahPlan?,
    onOpenIndex: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenKhatmah: () -> Unit,
    onOpenDailyProgress: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenGoToPage: () -> Unit,
    onToggleAudio: () -> Unit,
    onOpenReciterDialog: () -> Unit,
    onOpenPlaylists: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleNightMode: () -> Unit,
    onOpenNightShift: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val currentSurah = QuranData.getSurahForPage(currentPageNumber)
    val currentJuzNum = QuranData.getJuzNumberForPage(currentPageNumber)
    val currentJuzName = QuranData.getJuzName(currentJuzNum)

    fun closeAnd(action: () -> Unit) {
        scope.launch { drawerState.close() }
        action()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = QuranIndexBackground,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxHeight()
                    .testTag("quran_side_drawer")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                ) {
                    // Header Banner with Islamic Quran motif
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(QuranDarkGreen, Color(0xFF1B4D3E))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(QuranGold.copy(alpha = 0.25f), CircleShape)
                                            .border(1.dp, QuranGoldLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = QuranGoldLight,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "المصحف الشريف",
                                            fontFamily = AmiriFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "برواية حفص عن عاصم",
                                            fontFamily = AmiriFamily,
                                            fontSize = 12.sp,
                                            color = Color(0xFFC7D7D0)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { scope.launch { drawerState.close() } },
                                    modifier = Modifier.testTag("close_side_drawer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إغلاق القائمة",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Current Reading Position Pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .clickable { closeAnd { onOpenGoToPage() } }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "موضع القراءة الحالي:",
                                            fontFamily = AmiriFamily,
                                            fontSize = 11.sp,
                                            color = QuranGoldLight
                                        )
                                        Text(
                                            text = "${currentSurah.fullName} • صفحة ${QuranData.toArabicDigits(currentPageNumber)}",
                                            fontFamily = AmiriFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }

                                    Text(
                                        text = currentJuzName,
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE0E0E0)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SECTION 1: الأساسيات والتنقل (Navigation & Core Features)
                    DrawerSectionTitle("القرآن الكريم والتنقل")

                    // 1. فهرس السور والأجزاء
                    DrawerMenuItem(
                        icon = Icons.Default.FormatListNumbered,
                        title = "فهرس القرآن الكريم",
                        subtitle = "السور، الأجزاء، والصفحات",
                        badgeText = "١١٤ سورة",
                        testTag = "drawer_item_index",
                        onClick = { closeAnd { onOpenIndex() } }
                    )

                    // 2. البحث في القرآن الكريم
                    DrawerMenuItem(
                        icon = Icons.Default.Search,
                        title = "البحث في القرآن",
                        subtitle = "بالاسم، الرقم، أو الكلمة القرآنية",
                        testTag = "drawer_item_search",
                        onClick = { closeAnd { onOpenSearch() } }
                    )

                    // 3. ختمة القرآن الكريم
                    val khatmahBadge = if (khatmahPlan != null && khatmahPlan.isActive) {
                        val percent = ((khatmahPlan.currentProgressPage.toFloat() / 604f) * 100).toInt()
                        "${QuranData.toArabicDigits(percent)}%"
                    } else null

                    DrawerMenuItem(
                        icon = Icons.Default.MenuBook,
                        title = "ختمة القرآن الكريم",
                        subtitle = if (khatmahPlan != null && khatmahPlan.isActive) {
                            "خطة الـ ${QuranData.toArabicDigits(khatmahPlan.durationDays)} يوماً • ورد اليوم: ${QuranData.toArabicDigits(ceil(604.0 / khatmahPlan.durationDays).toInt())} ص"
                        } else {
                            "حدد مدة الختمة وتتبع وردك اليومي"
                        },
                        badgeText = khatmahBadge,
                        badgeColor = Color(0xFFE67E22),
                        testTag = "drawer_item_khatmah",
                        onClick = { closeAnd { onOpenKhatmah() } }
                    )

                    // 4. الورد اليومي وإنجاز القراءة
                    val wirdProgressPercent = if (dailyTargetGoal > 0) {
                        ((dailyPagesRead.toFloat() / dailyTargetGoal.toFloat()) * 100).toInt().coerceAtMost(100)
                    } else 0

                    DrawerMenuItem(
                        icon = Icons.Default.EmojiEvents,
                        title = "الورد اليومي والإنجاز",
                        subtitle = "قرأت اليوم ${QuranData.toArabicDigits(dailyPagesRead)} من أصل ${QuranData.toArabicDigits(dailyTargetGoal)} صفحات",
                        badgeText = "${QuranData.toArabicDigits(wirdProgressPercent)}%",
                        badgeColor = QuranDarkGreen,
                        testTag = "drawer_item_daily_progress",
                        onClick = { closeAnd { onOpenDailyProgress() } }
                    )

                    // 5. العلامات المرجعية والآيات المفضلة
                    DrawerMenuItem(
                        icon = Icons.Filled.Bookmark,
                        title = "العلامات والمفضلة",
                        subtitle = "الصفحات المحفوظة (${QuranData.toArabicDigits(bookmarksCount)}) • المفضلة (${QuranData.toArabicDigits(favoriteAyahsCount)})",
                        testTag = "drawer_item_bookmarks",
                        onClick = { closeAnd { onOpenBookmarks() } }
                    )

                    // 6. الانتقال السريع لصفحة
                    DrawerMenuItem(
                        icon = Icons.Default.Numbers,
                        title = "الانتقال المباشر لصفحة",
                        subtitle = "اكتب رقم أي صفحة (١ - ٦٠٤)",
                        testTag = "drawer_item_go_to_page",
                        onClick = { closeAnd { onOpenGoToPage() } }
                    )

                    // 7. حفظ/إزالة إشارة الصفحة الحالية
                    DrawerMenuItem(
                        icon = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        title = if (isBookmarked) "إزالة علامة الصفحة الحالية" else "حفظ الصفحة الحالية كعلامة",
                        subtitle = "صفحة ${QuranData.toArabicDigits(currentPageNumber)} في سورة ${currentSurah.name}",
                        iconTint = if (isBookmarked) QuranGoldBanner else QuranTextDark,
                        testTag = "drawer_item_toggle_bookmark",
                        onClick = {
                            onToggleBookmark()
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color(0xFFE2D6C0)
                    )

                    // SECTION 2: التلاوات الصوتية (Audio Recitations)
                    DrawerSectionTitle("التلاوات الصوتية")

                    // 8. الاستماع للتلاوة
                    DrawerMenuItem(
                        icon = if (isPlayingAudio) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                        title = if (isPlayingAudio) "إيقاف التلاوة مؤقتاً" else "تشغيل تلاوة الصفحة الحالية",
                        subtitle = "القارئ: $currentReciterName",
                        iconTint = if (isPlayingAudio) QuranGoldBanner else QuranTextDark,
                        testTag = "drawer_item_audio_toggle",
                        onClick = {
                            onToggleAudio()
                        }
                    )

                    // 9. اختيار القارئ
                    DrawerMenuItem(
                        icon = Icons.Default.Person,
                        title = "اختيار القارئ المفضل",
                        subtitle = currentReciterName,
                        testTag = "drawer_item_change_reciter",
                        onClick = { closeAnd { onOpenReciterDialog() } }
                    )

                    // 10. قوائم التلاوات المفضلة
                    DrawerMenuItem(
                        icon = Icons.Default.QueueMusic,
                        title = "قوائم التلاوات",
                        subtitle = "إدارة وحفظ تلاواتك المفضلة",
                        testTag = "drawer_item_playlists",
                        onClick = { closeAnd { onOpenPlaylists() } }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color(0xFFE2D6C0)
                    )

                    // SECTION 3: المظهر وراحة العين (Appearance & Comfort)
                    DrawerSectionTitle("المظهر وراحة القراءة")

                    // 11. الوضع الليلي / الفاتح
                    DrawerSwitchItem(
                        icon = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        title = "الوضع الليلي (المظلم)",
                        subtitle = if (isNightMode) "مفعّل - وضع القراءة في الإضاءة الخافتة" else "معطّل - وضع الورق العتيق الأصلي",
                        isChecked = isNightMode,
                        onCheckedChange = { onToggleNightMode() },
                        testTag = "drawer_night_mode_switch"
                    )

                    // 12. درع حماية العين (Night Shift)
                    DrawerMenuItem(
                        icon = Icons.Default.WbTwilight,
                        title = "درع حماية العين (Night Shift)",
                        subtitle = if (isNightShiftActive) "مفعّل - فلتر دافئ مريح للعين" else "إعدادات الفلتر الدافئ للإضاءة الليلية",
                        badgeText = if (isNightShiftActive) "نشط" else null,
                        badgeColor = Color(0xFFFF9E1B),
                        testTag = "drawer_item_night_shift",
                        onClick = { closeAnd { onOpenNightShift() } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Decorative Footer with Quran Ayah
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .background(Color(0xFFFAF6EF), RoundedCornerShape(12.dp))
                            .border(1.dp, QuranGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "﴿ وَرَتِّلِ ٱلۡقُرۡءَانَ تَرۡتِيلًا ﴾",
                                fontFamily = AmiriQuranFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = QuranGoldBanner,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "سورة المزمل - آية ٤",
                                fontFamily = AmiriFamily,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = QuranGoldBanner,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = QuranGoldBanner,
    iconTint: Color = QuranTextDark,
    testTag: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE2D6C0), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = QuranTextDark
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontFamily = AmiriFamily,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = badgeColor
                )
            }
        }
    }
}

@Composable
private fun DrawerSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE2D6C0), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = QuranTextDark,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = QuranTextDark
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontFamily = AmiriFamily,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = QuranGoldBanner
            )
        )
    }
}
