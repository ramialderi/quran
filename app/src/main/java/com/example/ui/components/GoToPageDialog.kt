package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranTextDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoToPageDialog(
    currentPage: Int,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onPageSelected: (Int) -> Unit
) {
    if (!isOpen) return

    var pageInputText by remember(currentPage) { mutableStateOf(currentPage.toString()) }
    var sliderValue by remember(currentPage) { mutableFloatStateOf(currentPage.toFloat()) }

    val resolvedPage = pageInputText.toIntOrNull()?.coerceIn(1, 604) ?: sliderValue.toInt().coerceIn(1, 604)
    val surahInfo = QuranData.getSurahForPage(resolvedPage)
    val juzNum = QuranData.getJuzNumberForPage(resolvedPage)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("go_to_page_dialog"),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color(0xFFFCF9F0),
        title = {
            Text(
                text = "الانتقال إلى صفحة في المصحف",
                fontFamily = AmiriFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = QuranTextDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Box: shows page, surah name, and Juz
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFE9D7), RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "صفحة ${QuranData.toArabicDigits(resolvedPage)} من ${QuranData.toArabicDigits(604)}",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF22301D)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${surahInfo.fullName} • ${QuranData.getJuzName(juzNum)}",
                            fontFamily = AmiriFamily,
                            fontSize = 14.sp,
                            color = Color(0xFF555047)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stepper + Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val prev = (resolvedPage - 1).coerceAtLeast(1)
                            pageInputText = prev.toString()
                            sliderValue = prev.toFloat()
                        },
                        modifier = Modifier.testTag("page_dec_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "الصفحة السابقة",
                            tint = QuranGoldBanner
                        )
                    }

                    OutlinedTextField(
                        value = pageInputText,
                        onValueChange = { newStr ->
                            val filtered = newStr.filter { it.isDigit() }.take(3)
                            pageInputText = filtered
                            val num = filtered.toIntOrNull()
                            if (num != null && num in 1..604) {
                                sliderValue = num.toFloat()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val num = pageInputText.toIntOrNull()?.coerceIn(1, 604)
                                if (num != null) {
                                    onPageSelected(num)
                                    onDismiss()
                                }
                            }
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            color = QuranTextDark
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuranGold,
                            unfocusedBorderColor = Color(0xFFC7AF80),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .width(110.dp)
                            .testTag("page_number_input")
                    )

                    IconButton(
                        onClick = {
                            val next = (resolvedPage + 1).coerceAtMost(604)
                            pageInputText = next.toString()
                            sliderValue = next.toFloat()
                        },
                        modifier = Modifier.testTag("page_inc_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "الصفحة التالية",
                            tint = QuranGoldBanner
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fast Slider
                Slider(
                    value = sliderValue,
                    onValueChange = { newVal ->
                        sliderValue = newVal
                        pageInputText = newVal.toInt().toString()
                    },
                    valueRange = 1f..604f,
                    colors = SliderDefaults.colors(
                        thumbColor = QuranGold,
                        activeTrackColor = QuranGoldBanner,
                        inactiveTrackColor = Color(0xFFD8CEB8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick bookmarks / popular pages shortcuts
                Text(
                    text = "صفحات شائعة:",
                    fontFamily = AmiriFamily,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val shortcuts = listOf(
                        Pair("ص ١ الفاتحة", 1),
                        Pair("ص ٢ البقرة", 2),
                        Pair("ص ٥٠ آل عمران", 50),
                        Pair("ص ٧٦ النساء", 76),
                        Pair("ص ٢٩٣ الكهف", 293),
                        Pair("ص ٤٤٠ يس", 440),
                        Pair("ص ٥٨٢ جزء عم", 582),
                        Pair("ص ٦٠٤ الناس", 604)
                    )

                    shortcuts.forEach { (title, pageNum) ->
                        SuggestionChip(
                            onClick = {
                                pageInputText = pageNum.toString()
                                sliderValue = pageNum.toFloat()
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontFamily = AmiriFamily,
                                    fontSize = 12.sp
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (resolvedPage == pageNum) Color(0xFFE8DCC0) else Color.White
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = Color(0xFFD4C29D)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalPage = pageInputText.toIntOrNull()?.coerceIn(1, 604) ?: resolvedPage
                    onPageSelected(finalPage)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22301D)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_go_to_page")
            ) {
                Text(
                    text = "انتقال إلى صفحة ${QuranData.toArabicDigits(resolvedPage)}",
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_go_to_page")
            ) {
                Text(
                    text = "إلغاء",
                    fontFamily = AmiriFamily,
                    color = Color.Gray
                )
            }
        }
    )
}
