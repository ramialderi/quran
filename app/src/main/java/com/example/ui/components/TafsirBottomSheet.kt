package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.data.TafsirResult
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranNightBackground
import com.example.ui.theme.QuranNightParchment
import com.example.ui.theme.QuranNightText
import com.example.ui.theme.QuranParchment
import com.example.ui.theme.QuranTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirBottomSheet(
    tafsirResult: TafsirResult?,
    isOpen: Boolean,
    isNightMode: Boolean = false,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onPlayAudio: (surahId: Int, ayahNumber: Int) -> Unit,
    onNextAyah: () -> Unit = {},
    onPreviousAyah: () -> Unit = {},
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
) {
    if (!isOpen || tafsirResult == null) return

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var tafsirFontSize by remember { mutableFloatStateOf(16f) }

    val bgColor = if (isNightMode) QuranNightBackground else QuranIndexBackground
    val cardBg = if (isNightMode) QuranNightParchment else Color.White
    val textColor = if (isNightMode) QuranNightText else QuranTextDark

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        dragHandle = null,
        modifier = Modifier.testTag("tafsir_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        ) {
            // Gold Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QuranGoldBanner)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
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
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "تفسير الآية الكريمة",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${tafsirResult.surahName} • آية ${QuranData.toArabicDigits(tafsirResult.ayahNumber)}",
                            fontFamily = AmiriFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("close_tafsir_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق التفسير",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Verse Card (Full verse text in authentic Quranic script)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, if (isNightMode) Color(0xFF2E382C) else QuranGold.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نص الآية الكريمة",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = QuranGoldBanner
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Favorite / Bookmark Ayah Button
                                Button(
                                    onClick = onToggleFavorite,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFavorite) Color(0xFFFFF3E0) else Color(0xFFFAF6EF)
                                    ),
                                    border = BorderStroke(1.dp, if (isFavorite) Color(0xFFE67E22) else Color(0xFFDCCFBB)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("toggle_favorite_ayah_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isFavorite) Color(0xFFE67E22) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isFavorite) "في المفضلة" else "حفظ بالمفضلة",
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = if (isFavorite) Color(0xFFE65100) else QuranTextDark
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Quick Play Audio Button
                                Button(
                                    onClick = {
                                        onPlayAudio(tafsirResult.surahId, tafsirResult.ayahNumber)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = QuranGoldBanner),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("play_ayah_from_tafsir_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "استمع للآية",
                                        fontFamily = AmiriFamily,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Verse Text with Golden Marker
                        val surah = QuranData.surahs.find { it.id == tafsirResult.surahId }
                        val foundVerse = surah?.let { s ->
                            var result: String? = null
                            for (p in s.startPage..s.endPage) {
                                val pageData = QuranData.getPage(p)
                                val v = pageData.verses.find { it.ayahNumber == tafsirResult.ayahNumber }
                                if (v != null) {
                                    result = v.text
                                    break
                                }
                            }
                            result
                        }
                        val verseDisplay = foundVerse ?: "﴿ ${tafsirResult.surahName} ﴾"

                        Text(
                            text = "$verseDisplay ﴿${QuranData.toArabicDigits(tafsirResult.ayahNumber)}﴾",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            lineHeight = 32.sp,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tafsir Header & Controls (Font size adjustment & Copy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التفسير الميسر (مجمع الملك فهد):",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = QuranGoldBanner
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Font Size Decrease (-)
                        IconButton(
                            onClick = {
                                if (tafsirFontSize > 13f) tafsirFontSize -= 1.5f
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text(
                                text = "A-",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // Font Size Increase (+)
                        IconButton(
                            onClick = {
                                if (tafsirFontSize < 24f) tafsirFontSize += 1.5f
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text(
                                text = "A+",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = QuranGoldBanner
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Copy Button
                        IconButton(
                            onClick = {
                                val textToCopy = "سورة ${tafsirResult.surahName} [آية ${tafsirResult.ayahNumber}]:\n\nتفسير:\n${tafsirResult.text}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("تفسير الآية", textToCopy)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ التفسير إلى الحافظة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ التفسير",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tafsir Body Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, if (isNightMode) Color(0xFF2E382C) else Color(0xFFE2D6C0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (tafsirResult.isLoading) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = QuranGoldBanner,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "جاري تحميل التفسير الميسر...",
                                    fontFamily = AmiriFamily,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            Text(
                                text = tafsirResult.text.ifBlank { "لم يتوفر نص التفسير لهذه الآية حالياً." },
                                fontFamily = AmiriFamily,
                                fontSize = tafsirFontSize.sp,
                                lineHeight = (tafsirFontSize * 1.65).sp,
                                color = textColor,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation between verses for Tafsir (الآية السابقة والآية التالية)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPreviousAyah,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isNightMode) Color(0xFF2E382C) else QuranGold.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isNightMode) QuranNightParchment else Color(0xFFFAF6EF),
                            contentColor = textColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tafsir_prev_ayah_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = QuranGoldBanner
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الآية السابقة",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedButton(
                        onClick = onNextAyah,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isNightMode) Color(0xFF2E382C) else QuranGold.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isNightMode) QuranNightParchment else Color(0xFFFAF6EF),
                            contentColor = textColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tafsir_next_ayah_button")
                    ) {
                        Text(
                            text = "الآية التالية",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = QuranGoldBanner
                        )
                    }
                }
            }
        }
    }
}
