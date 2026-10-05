package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranDarkGreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranNightBackground
import com.example.ui.theme.QuranNightParchment
import com.example.ui.theme.QuranNightText
import com.example.ui.theme.QuranTextDark

@Composable
fun AyahActionBar(
    visible: Boolean,
    selectedAyahKey: String?,
    isNightMode: Boolean,
    isFavorite: Boolean,
    onOpenTafsir: (surahId: Int, ayahNumber: Int) -> Unit,
    onPlayAyah: (surahId: Int, ayahNumber: Int) -> Unit,
    onToggleFavorite: (surahId: Int, ayahNumber: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && selectedAyahKey != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        if (selectedAyahKey == null) return@AnimatedVisibility

        val context = LocalContext.current
        val parts = selectedAyahKey.split(":")
        val surahId = parts.getOrNull(0)?.toIntOrNull() ?: 1
        val ayahNumber = parts.getOrNull(1)?.toIntOrNull() ?: 1
        val surah = QuranData.surahs.find { it.id == surahId } ?: QuranData.surahs[0]

        val cardBg = if (isNightMode) QuranNightParchment else Color(0xFFFCF9F3)
        val borderColor = if (isNightMode) QuranGold.copy(alpha = 0.5f) else Color(0xFFDCCFBB)
        val textColor = if (isNightMode) QuranNightText else QuranTextDark

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                .testTag("ayah_action_bar"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                // Top row: Surah and Ayah info badge + close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(QuranGold.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, QuranGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = QuranData.toArabicDigits(ayahNumber),
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = QuranGoldBanner
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${surah.fullName} • آية ${QuranData.toArabicDigits(ayahNumber)}",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isNightMode) QuranGold else QuranDarkGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_ayah_action_bar")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إلغاء التحديد",
                            tint = if (isNightMode) Color(0xFFB0A48E) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom row: Action Buttons (Tafsir, Audio, Favorite, Copy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Tafsir Button
                    AyahActionButton(
                        icon = Icons.Default.MenuBook,
                        label = "التفسير",
                        isHighlight = true,
                        testTag = "ayah_action_tafsir",
                        onClick = { onOpenTafsir(surahId, ayahNumber) }
                    )

                    // 2. Audio Playback Button
                    AyahActionButton(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        label = "استماع",
                        isHighlight = false,
                        testTag = "ayah_action_play",
                        onClick = { onPlayAyah(surahId, ayahNumber) }
                    )

                    // 3. Favorite Button
                    AyahActionButton(
                        icon = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        label = if (isFavorite) "المفضلة" else "حفظ",
                        isHighlight = false,
                        iconTint = if (isFavorite) Color(0xFFE67E22) else null,
                        testTag = "ayah_action_favorite",
                        onClick = { onToggleFavorite(surahId, ayahNumber) }
                    )

                    // 4. Copy Ayah Text Button
                    AyahActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "نسخ",
                        isHighlight = false,
                        testTag = "ayah_action_copy",
                        onClick = {
                            var verseText = ""
                            for (p in surah.startPage..surah.endPage) {
                                val pageData = QuranData.getPage(p)
                                val v = pageData.verses.find { it.ayahNumber == ayahNumber }
                                if (v != null) {
                                    verseText = v.text
                                    break
                                }
                            }
                            val textToCopy = if (verseText.isNotBlank()) {
                                "﴿ $verseText ﴾ [${surah.fullName} : ${QuranData.toArabicDigits(ayahNumber)}]"
                            } else {
                                "[${surah.fullName} - آية ${QuranData.toArabicDigits(ayahNumber)}]"
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("آية كريمة", textToCopy)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ الآية الكريمة إلى الحافظة", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AyahActionButton(
    icon: ImageVector,
    label: String,
    isHighlight: Boolean = false,
    iconTint: Color? = null,
    testTag: String,
    onClick: () -> Unit
) {
    val bg = if (isHighlight) QuranGoldBanner else Color.Transparent
    val border = if (isHighlight) null else BorderStroke(1.dp, Color(0xFFDCCFBB))
    val contentColor = if (isHighlight) Color.White else (iconTint ?: QuranDarkGreen)

    Row(
        modifier = Modifier
            .background(
                color = bg,
                shape = RoundedCornerShape(10.dp)
            )
            .then(
                if (border != null) Modifier.border(border, RoundedCornerShape(10.dp)) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontFamily = AmiriFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = if (isHighlight) Color.White else QuranTextDark
        )
    }
}
