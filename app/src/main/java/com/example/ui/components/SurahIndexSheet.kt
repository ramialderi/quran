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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.data.SurahInfo
import com.example.data.UserBookmark
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranCalligraphyFamily
import com.example.ui.theme.QuranDivider
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranTextDark

private object SurahSearchHelper {
    private val tashkeelRegex = Regex("[\u064B-\u065F\u0670]")
    private val alefRegex = Regex("[أإآ]")
    private val surahPrefixRegex = Regex("^(سورة|سوره)\\s*")

    fun normalize(text: String): String {
        return text
            .replace(tashkeelRegex, "")
            .replace(alefRegex, "ا")
            .replace('ة', 'ه')
            .replace('ى', 'ي')
            .replace('ؤ', 'و')
            .replace('ئ', 'ي')
            .replace(surahPrefixRegex, "")
            .trim()
            .lowercase()
    }

    private val digitMap = mapOf(
        '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
        '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
    )

    fun toStandardDigits(text: String): String {
        return text.map { digitMap[it] ?: it }.joinToString("")
    }

    fun matches(surah: SurahInfo, rawQuery: String): Boolean {
        val q = rawQuery.trim()
        if (q.isBlank()) return true

        val qNorm = normalize(q)
        val qNoAl = qNorm.removePrefix("ال")
        val qDigits = toStandardDigits(q).filter { it.isDigit() }

        val nameNorm = normalize(surah.name)
        val nameNoAl = nameNorm.removePrefix("ال")
        val fullNorm = normalize(surah.fullName)

        // Name match (with or without 'ال' prefix, with or without 'سورة')
        if (qNorm.isNotEmpty() && (nameNorm.contains(qNorm) || nameNoAl.contains(qNoAl) || fullNorm.contains(qNorm))) {
            return true
        }

        // Surah ID or Start Page or Juz match if digits typed
        if (qDigits.isNotEmpty()) {
            if (surah.id.toString() == qDigits ||
                surah.startPage.toString() == qDigits ||
                surah.juzNumber.toString() == qDigits
            ) {
                return true
            }
        }

        // Revelation type match (مكية أو مدنية)
        if (qNorm.isNotEmpty() && surah.revelationType.contains(qNorm)) {
            return true
        }

        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahIndexSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSurahClick: (SurahInfo) -> Unit,
    onPageClick: (Int) -> Unit = {},
    bookmarks: List<UserBookmark> = emptyList(),
    onDeleteBookmark: (Int) -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isOpen) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuranIndexBackground,
        dragHandle = null,
        modifier = Modifier.testTag("surah_index_sheet")
    ) {
        var selectedTab by remember { mutableIntStateOf(0) }
        var searchQuery by remember { mutableStateOf("") }

        val filteredSurahs = remember(searchQuery) {
            if (searchQuery.isBlank()) {
                QuranData.surahs
            } else {
                QuranData.surahs.filter { SurahSearchHelper.matches(it, searchQuery) }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(QuranIndexBackground)
        ) {
            // Gold Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QuranGoldBanner)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "فهرس القرآن الكريم",
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("close_index_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White
                    )
                }
            }

            // Navigation Tabs: السور ، الصفحات ، الإشارات المرجعية
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF3ECE0),
                contentColor = QuranGoldBanner,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = QuranGoldBanner
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "السور",
                            fontFamily = AmiriFamily,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "الصفحات (٦٠٤)",
                            fontFamily = AmiriFamily,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "الإشارات",
                                fontFamily = AmiriFamily,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                            if (bookmarks.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(QuranGoldBanner, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = QuranData.toArabicDigits(bookmarks.size),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "ابحث باسم السورة (الكهف، يس...) أو رقمها...",
                                fontFamily = AmiriFamily,
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = QuranGoldBanner
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.testTag("clear_search_query_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح البحث",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("surah_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuranGoldBanner,
                            unfocusedBorderColor = QuranDivider,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Quick Search Filter Chips
                    val quickFilterChips = listOf("الكهف", "يس", "الملك", "الواقعة", "البقرة", "مكية", "مدنية")
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickFilterChips) { chipName ->
                            val isSelected = searchQuery.trim() == chipName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) QuranGoldBanner else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) QuranGoldBanner else Color(0xFFDCCFBB),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        searchQuery = if (isSelected) "" else chipName
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                    .testTag("search_chip_$chipName")
                            ) {
                                Text(
                                    text = chipName,
                                    fontFamily = AmiriFamily,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else QuranTextDark
                                )
                            }
                        }
                    }

                    // Search Results Count Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (searchQuery.isNotBlank()) {
                            Text(
                                text = "نتائج البحث: ${QuranData.toArabicDigits(filteredSurahs.size)} سورة",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = QuranGoldBanner,
                                modifier = Modifier.testTag("search_results_count")
                            )

                            Text(
                                text = "مسح البحث",
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .clickable { searchQuery = "" }
                                    .testTag("clear_search_text_button")
                            )
                        } else {
                            Text(
                                text = "جميع سور القرآن الكريم (${QuranData.toArabicDigits(114)} سورة)",
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Surahs List or Empty State
                    if (filteredSurahs.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 24.dp)
                                .testTag("search_empty_state"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFFF1E7D3), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = QuranGoldBanner,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "لا توجد نتائج مطابقة",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = QuranTextDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "لم نعثر على أي سورة مطابقة لـ \"$searchQuery\"\nتأكد من كتابة اسم السورة أو رقمها",
                                fontFamily = AmiriFamily,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { searchQuery = "" },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, QuranGoldBanner),
                                modifier = Modifier.testTag("reset_search_empty_button")
                            ) {
                                Text(
                                    text = "عرض كافة السور",
                                    fontFamily = AmiriFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = QuranGoldBanner
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("surahs_list")
                        ) {
                            items(filteredSurahs, key = { it.id }) { surah ->
                                SurahIndexItem(
                                    surah = surah,
                                    onClick = {
                                        onSurahClick(surah)
                                        onDismiss()
                                    }
                                )
                                HorizontalDivider(
                                    color = QuranDivider.copy(alpha = 0.7f),
                                    thickness = 0.7.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Pages Grid (1 to 604)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "اختر الصفحة للانتقال المباشر إليها:",
                            fontFamily = AmiriFamily,
                            fontSize = 14.sp,
                            color = Color(0xFF6B5E43),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 56.dp),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(604) { index ->
                                val page = index + 1
                                val juzNum = QuranData.getJuzNumberForPage(page)
                                val isJuzStart = page % 20 == 2 || page == 1

                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            color = if (isJuzStart) Color(0xFFF1E6D0) else Color.White,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isJuzStart) QuranGold else Color(0xFFDCCFBB),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            onPageClick(page)
                                            onDismiss()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = QuranData.toArabicDigits(page),
                                            fontFamily = AmiriFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF22301D)
                                        )
                                        Text(
                                            text = "ج${QuranData.toArabicDigits(juzNum)}",
                                            fontFamily = AmiriFamily,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Bookmarks List
                    if (bookmarks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = QuranGoldBanner,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "لا توجد إشارات مرجعية بعد",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = QuranTextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "اضغط على أيقونة الإشارة المرجعية (🔖) في أعلى صفحة القراءة لحفظ أي صفحة والرجوع إليها بسرعة.",
                                fontFamily = AmiriFamily,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("index_bookmarks_list")
                        ) {
                            items(bookmarks, key = { it.page }) { bookmark ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            onPageClick(bookmark.page)
                                            onDismiss()
                                        }
                                        .testTag("index_bookmark_item_${bookmark.page}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmark,
                                                contentDescription = null,
                                                tint = QuranGoldBanner,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = bookmark.surahName,
                                                    fontFamily = AmiriFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 17.sp,
                                                    color = QuranTextDark
                                                )
                                                Text(
                                                    text = "${QuranData.getJuzName(bookmark.juzNumber)} • صفحة ${QuranData.toArabicDigits(bookmark.page)}",
                                                    fontFamily = AmiriFamily,
                                                    fontSize = 13.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteBookmark(bookmark.page) },
                                            modifier = Modifier.testTag("delete_index_bookmark_${bookmark.page}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "حذف الإشارة",
                                                tint = Color.Red.copy(alpha = 0.65f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahIndexItem(
    surah: SurahInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("surah_index_item_${surah.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Surah Number Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF3ECE0), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFC7AF80), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = QuranData.toArabicDigits(surah.id),
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = QuranTextDark
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = surah.fullName,
                    fontFamily = QuranCalligraphyFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = QuranTextDark
                )

                Text(
                    text = "${surah.revelationType} • ${QuranData.toArabicDigits(surah.ayahsCount)} آية",
                    fontFamily = AmiriFamily,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        // Start Page & Juz Info
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "صفحة ${QuranData.toArabicDigits(surah.startPage)}",
                fontFamily = AmiriFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = QuranGoldBanner
            )

            Text(
                text = QuranData.getJuzName(surah.juzNumber),
                fontFamily = AmiriFamily,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
