package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.data.SearchAyahResult
import com.example.data.SurahInfo
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.AmiriQuranFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranTextDark

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranSearchSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSurahClick: (SurahInfo) -> Unit,
    onAyahClick: (surahId: Int, ayahNumber: Int, pageNumber: Int) -> Unit,
    onPlayAyah: (surahId: Int, ayahNumber: Int) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isOpen) return

    var query by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: الكل, 1: السور, 2: الآيات
    val focusRequester = remember { FocusRequester() }

    val matchedSurahs = remember(query) {
        if (query.isBlank()) emptyList() else QuranData.searchSurahs(query)
    }

    val matchedAyahs = remember(query) {
        if (query.isBlank()) emptyList() else QuranData.searchAyahs(query)
    }

    val popularSuggestions = listOf(
        "سورة الكهف",
        "آية الكرسي",
        "سورة يس",
        "سورة الرحمن",
        "سورة الملك",
        "سورة الواقعة",
        "الفاتحة",
        "الإخلاص",
        "الحمد",
        "الجنة",
        "الصبر",
        "النور"
    )

    LaunchedEffect(isOpen) {
        if (isOpen) {
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuranIndexBackground,
        dragHandle = null,
        modifier = Modifier.testTag("quran_search_sheet")
    ) {
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "البحث في القرآن الكريم",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("close_search_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Search Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = "ابحث باسم السورة، رقم الآية، أو كلمة قرآنية...",
                            fontFamily = AmiriFamily,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = QuranGoldBanner
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "مسح",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = QuranGoldBanner,
                        unfocusedBorderColor = Color(0xFFDCCFBB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("quran_search_text_field")
                )
            }

            // Filter Tabs (الكل • السور • الآيات)
            if (query.isNotBlank()) {
                TabRow(
                    selectedTabIndex = selectedFilterIndex,
                    containerColor = Color(0xFFF3ECE0),
                    contentColor = QuranGoldBanner,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedFilterIndex]),
                            color = QuranGoldBanner,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedFilterIndex == 0,
                        onClick = { selectedFilterIndex = 0 },
                        text = {
                            Text(
                                text = "الكل (${QuranData.toArabicDigits(matchedSurahs.size + matchedAyahs.size)})",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (selectedFilterIndex == 0) QuranTextDark else Color.Gray
                            )
                        },
                        modifier = Modifier.testTag("filter_all_tab")
                    )

                    Tab(
                        selected = selectedFilterIndex == 1,
                        onClick = { selectedFilterIndex = 1 },
                        text = {
                            Text(
                                text = "السور (${QuranData.toArabicDigits(matchedSurahs.size)})",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (selectedFilterIndex == 1) QuranTextDark else Color.Gray
                            )
                        },
                        modifier = Modifier.testTag("filter_surahs_tab")
                    )

                    Tab(
                        selected = selectedFilterIndex == 2,
                        onClick = { selectedFilterIndex = 2 },
                        text = {
                            Text(
                                text = "الآيات (${QuranData.toArabicDigits(matchedAyahs.size)})",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (selectedFilterIndex == 2) QuranTextDark else Color.Gray
                            )
                        },
                        modifier = Modifier.testTag("filter_ayahs_tab")
                    )
                }
            }

            // Search Content / Results
            if (query.isBlank()) {
                // Empty State with Search Suggestions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFE67E22),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اقتراحات بحث سريعة وشائعة:",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = QuranTextDark
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        popularSuggestions.forEach { suggestion ->
                            AssistChip(
                                onClick = { query = suggestion },
                                label = {
                                    Text(
                                        text = suggestion,
                                        fontFamily = AmiriFamily,
                                        fontSize = 13.sp,
                                        color = QuranTextDark
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2D6C0)),
                                modifier = Modifier.testTag("suggestion_chip_$suggestion")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF9EE), RoundedCornerShape(14.dp))
                            .border(1.dp, QuranGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "💡 نصائح للبحث:",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = QuranGoldBanner
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• للبحث عن سورة: اكتب اسمها (مثل: الكهف أو سورة البقرة) أو رقمها (مثل: 18 أو 1).\n• للبحث عن آية: اكتب رقماً (مثل: 255) أو كلمة قرآنية (مثل: الحمد، الصراط، الرحمن).",
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                color = QuranTextDark,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            } else {
                val showSurahs = selectedFilterIndex == 0 || selectedFilterIndex == 1
                val showAyahs = selectedFilterIndex == 0 || selectedFilterIndex == 2
                val hasAnyResults = (showSurahs && matchedSurahs.isNotEmpty()) || (showAyahs && matchedAyahs.isNotEmpty())

                if (!hasAnyResults) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "لا توجد نتائج مطابقة لـ \"$query\"",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = QuranTextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تأكد من كتابة الاسم أو الكلمة بشكل صحيح، أو جرّب كتابة رقم السورة أو الآية.",
                                fontFamily = AmiriFamily,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        // Section 1: Matched Surahs
                        if (showSurahs && matchedSurahs.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = QuranGoldBanner,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "السور المطابقة (${QuranData.toArabicDigits(matchedSurahs.size)}):",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = QuranGoldBanner
                                    )
                                }
                            }

                            items(matchedSurahs, key = { "surah_${it.id}" }) { surah ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onSurahClick(surah) }
                                        .testTag("search_surah_result_${surah.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Surah Number Gold Badge
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Color(0xFFFAF6EF), CircleShape)
                                                    .border(1.dp, QuranGold, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = QuranData.toArabicDigits(surah.id),
                                                    fontFamily = AmiriFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = QuranTextDark
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = surah.fullName,
                                                    fontFamily = AmiriFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 17.sp,
                                                    color = QuranTextDark
                                                )
                                                Text(
                                                    text = "${surah.revelationType} • ${QuranData.toArabicDigits(surah.ayahsCount)} آيات • ${QuranData.getJuzName(surah.juzNumber)}",
                                                    fontFamily = AmiriFamily,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Text(
                                            text = "صفحة ${QuranData.toArabicDigits(surah.startPage)}",
                                            fontFamily = AmiriFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = QuranGoldBanner
                                        )
                                    }
                                }
                            }
                        }

                        // Section 2: Matched Ayahs
                        if (showAyahs && matchedAyahs.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFE67E22),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "الآيات المطابقة (${QuranData.toArabicDigits(matchedAyahs.size)}):",
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFFE67E22)
                                    )
                                }
                            }

                            items(matchedAyahs, key = { "ayah_${it.surahId}_${it.ayahNumber}_${it.pageNumber}" }) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .testTag("search_ayah_result_${item.surahId}_${item.ayahNumber}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        // Header: Surah name & Ayah number
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.surahName} • آية ${QuranData.toArabicDigits(item.ayahNumber)}",
                                                fontFamily = AmiriFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = QuranTextDark
                                            )

                                            Text(
                                                text = "صفحة ${QuranData.toArabicDigits(item.pageNumber)}",
                                                fontFamily = AmiriFamily,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Verse text
                                        Text(
                                            text = "${item.text} ﴿${QuranData.toArabicDigits(item.ayahNumber)}﴾",
                                            fontFamily = AmiriQuranFamily,
                                            fontSize = 15.sp,
                                            lineHeight = 26.sp,
                                            color = QuranTextDark,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFFAF6EF), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Action buttons: Navigate & Play
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            // Play Audio Button
                                            Button(
                                                onClick = { onPlayAyah(item.surahId, item.ayahNumber) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAF6EF)),
                                                border = BorderStroke(1.dp, QuranGold),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = null,
                                                    tint = QuranGoldBanner,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("استماع", fontFamily = AmiriFamily, fontSize = 11.sp, color = QuranTextDark)
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Navigate to Ayah
                                            Button(
                                                onClick = { onAyahClick(item.surahId, item.ayahNumber, item.pageNumber) },
                                                colors = ButtonDefaults.buttonColors(containerColor = QuranGoldBanner),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text("الانتقال للآية", fontFamily = AmiriFamily, fontSize = 11.sp, color = Color.White)
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
}
