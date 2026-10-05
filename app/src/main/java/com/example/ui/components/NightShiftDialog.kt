package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranTextDark

@Composable
fun NightShiftDialog(
    isOpen: Boolean,
    isNightShiftActive: Boolean,
    isAutoEnabled: Boolean,
    isManualEnabled: Boolean,
    warmthLevel: Float,
    onToggleAuto: (Boolean) -> Unit,
    onToggleManual: (Boolean) -> Unit,
    onWarmthChanged: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val warmthPercent = (warmthLevel * 100).toInt()

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
                    .testTag("night_shift_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFFFF9E1B).copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbTwilight,
                                contentDescription = null,
                                tint = Color(0xFFE67E22),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "درع حماية العين (Night Shift)",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = QuranTextDark
                            )
                            Text(
                                text = "إضاءة دافئة للقراءة الليلية الهادئة",
                                fontFamily = AmiriFamily,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_night_shift_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Status Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isNightShiftActive) Color(0xFFFFF3E0) else Color(0xFFF0EAE1),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isNightShiftActive) Color(0xFFFFB74D) else Color(0xFFDCCFBB),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isNightShiftActive) {
                            "🌙 درع العين نشط حالياً (تقليل الضوء الأزرق بنسبة ${QuranData.toArabicDigits(warmthPercent)}٪)"
                        } else {
                            "☀️ درع العين في وضع الاستعداد (ينشط تلقائياً في المساء)"
                        },
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isNightShiftActive) Color(0xFFE65100) else Color(0xFF5D5343),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Switch 1: Auto Schedule (Time of day)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "التشغيل التلقائي حسب الوقت",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = QuranTextDark
                        )
                        Text(
                            text = "تفعيل الدفء تلقائياً بين ٧:٠٠ م و ٦:٠٠ ص",
                            fontFamily = AmiriFamily,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isAutoEnabled,
                        onCheckedChange = onToggleAuto,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFE67E22)
                        ),
                        modifier = Modifier.testTag("night_shift_auto_switch")
                    )
                }

                HorizontalDivider(
                    color = Color(0xFFEADBCE),
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Switch 2: Manual Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تشغيل فوري يدوي",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = QuranTextDark
                        )
                        Text(
                            text = "تفعيل الإضاءة الدافئة فوراً بغض النظر عن الوقت",
                            fontFamily = AmiriFamily,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isManualEnabled,
                        onCheckedChange = onToggleManual,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFE67E22)
                        ),
                        modifier = Modifier.testTag("night_shift_manual_switch")
                    )
                }

                HorizontalDivider(
                    color = Color(0xFFEADBCE),
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Warmth Intensity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "درجة دفء الإضاءة (Warmth):",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = QuranTextDark
                    )

                    Text(
                        text = "${QuranData.toArabicDigits(warmthPercent)}٪",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFE65100)
                    )
                }

                Slider(
                    value = warmthLevel,
                    onValueChange = onWarmthChanged,
                    valueRange = 0.10f..0.45f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFE67E22),
                        activeTrackColor = Color(0xFFE67E22),
                        inactiveTrackColor = Color(0xFFEADBCE)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("night_shift_warmth_slider")
                )

                // Preview bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEDE3D0))
                        .border(1.dp, Color(0xFFDCCFBB), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xFFFF9E1B).copy(alpha = warmthLevel))
                    )
                    Text(
                        text = "معاينة درجة الدفء المريحة للعين",
                        fontFamily = AmiriFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuranTextDark
                    )
                }
            }
        }
    )
}
