package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FavoriteAyah
import com.example.data.QuranData
import com.example.data.UserBookmark
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.AmiriQuranFamily
import com.example.ui.theme.QuranCardHeader
import com.example.ui.theme.QuranDivider
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksSheet(
    isOpen: Boolean,
    bookmarks: List<UserBookmark>,
    favoriteAyahs: List<FavoriteAyah>,
    onDismiss: () -> Unit,
    onBookmarkClick: (Int) -> Unit,
    onDeleteBookmark: (Int) -> Unit,
    onFavoriteAyahClick: (FavoriteAyah) -> Unit,
    onPlayFavoriteAyah: (FavoriteAyah) -> Unit,
    onDeleteFavoriteAyah: (Long) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isOpen) return

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Favorite Ayahs, 1: Page Bookmarks

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuranIndexBackground,
        dragHandle = null,
        modifier = Modifier.testTag("bookmarks_sheet")
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
                    .background(QuranCardHeader)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "العلامات والمفضلة",
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("close_bookmarks_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White
                    )
                }
            }

            // Tab Row: Favorite Ayahs (⭐) vs Page Bookmarks (🔖)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFF3ECE0),
                contentColor = QuranGoldBanner,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = QuranGoldBanner,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTabIndex == 0) Color(0xFFE67E22) else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الآيات المفضلة (${QuranData.toArabicDigits(favoriteAyahs.size)})",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (selectedTabIndex == 0) QuranTextDark else Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_favorite_ayahs")
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTabIndex == 1) QuranGold else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "علامات الصفحات (${QuranData.toArabicDigits(bookmarks.size)})",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (selectedTabIndex == 1) QuranTextDark else Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_page_bookmarks")
                )
            }

            // Tab Content
            if (selectedTabIndex == 0) {
                // Tab 0: Favorite Ayahs
                if (favoriteAyahs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "قائمة الآيات المفضلة فارغة حالياً",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = QuranTextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "يمكنك حفظ أي آية في المفضلة بالضغط المطول عليها لفتح التفسير ثم الضغط على زر (حفظ في المفضلة ⭐).",
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
                        items(favoriteAyahs, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .testTag("favorite_ayah_card_${item.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Top Row: Surah & Ayah badge + Page Number + Delete Button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFE67E22),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${item.surahName} • آية ${QuranData.toArabicDigits(item.ayahNumber)}",
                                                fontFamily = AmiriFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = QuranTextDark
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "صفحة ${QuranData.toArabicDigits(item.pageNumber)}",
                                                fontFamily = AmiriFamily,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            IconButton(
                                                onClick = { onDeleteFavoriteAyah(item.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "حذف من المفضلة",
                                                    tint = Color.Red.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Verse Text in Quranic Calligraphy
                                    Text(
                                        text = "${item.text} ﴿${QuranData.toArabicDigits(item.ayahNumber)}﴾",
                                        fontFamily = AmiriQuranFamily,
                                        fontSize = 16.sp,
                                        lineHeight = 28.sp,
                                        color = QuranTextDark,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFFAF6EF), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Action buttons: Navigate to Ayah & Play Audio
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        // Play Audio Button
                                        Button(
                                            onClick = { onPlayFavoriteAyah(item) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAF6EF)),
                                            border = BorderStroke(1.dp, QuranGold),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = null,
                                                tint = QuranGoldBanner,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("استماع", fontFamily = AmiriFamily, fontSize = 12.sp, color = QuranTextDark)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Navigate to Ayah
                                        Button(
                                            onClick = { onFavoriteAyahClick(item) },
                                            colors = ButtonDefaults.buttonColors(containerColor = QuranGoldBanner),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier
                                                .height(34.dp)
                                                .testTag("navigate_to_favorite_ayah_${item.id}")
                                        ) {
                                            Text("الانتقال للآية", fontFamily = AmiriFamily, fontSize = 12.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab 1: Page Bookmarks
                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = QuranGold.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد علامات مرجعية محفوظة بعد",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = QuranTextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اضغط على أيقونة الإشارة المرجعية في أعلى الشاشة لحفظ الصفحة الحالية للعودة إليها لاحقاً.",
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
                        items(bookmarks, key = { it.page }) { bookmark ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onBookmarkClick(bookmark.page) }
                                    .testTag("bookmark_item_${bookmark.page}"),
                                shape = RoundedCornerShape(12.dp),
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
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = QuranGold,
                                            modifier = Modifier.size(24.dp)
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
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "حذف العلامة",
                                            tint = Color.Red.copy(alpha = 0.6f),
                                            modifier = Modifier.size(20.dp)
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
