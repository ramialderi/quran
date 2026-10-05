package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.AmiriQuranFamily
import com.example.ui.theme.QuranAyahGold
import com.example.ui.theme.QuranAyahGreen
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner

// Pointed Islamic Mihrab Arch Shape (used in Top Header as seen in Screenshot 1)
val MihrabArchShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val pointY = 0f
    val shoulderY = h * 0.35f

    moveTo(0f, h)
    lineTo(0f, shoulderY)
    // Curve to pointed tip
    cubicTo(0f, shoulderY * 0.4f, w * 0.3f, pointY, w * 0.5f, pointY)
    cubicTo(w * 0.7f, pointY, w, shoulderY * 0.4f, w, shoulderY)
    lineTo(w, h)
    close()
}

@Composable
fun MihrabArchHeader(
    surahName: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF22311D),
    borderColor: Color = QuranGold
) {
    Box(
        modifier = modifier
            .background(backgroundColor, shape = MihrabArchShape)
            .border(1.5.dp, borderColor, shape = MihrabArchShape)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = surahName,
            fontFamily = AmiriFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

// Circular Ayah End Marker with decorative gold ring and number
@Composable
fun AyahMarker(
    ayahNumber: Int,
    modifier: Modifier = Modifier,
    isNightMode: Boolean = false
) {
    val goldColor = if (isNightMode) QuranGold else QuranAyahGold
    val innerColor = if (isNightMode) Color(0xFF7FA884) else QuranAyahGreen

    Box(
        modifier = modifier
            .size(26.dp)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val r = size.minDimension / 2f
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Outer decorative floral dots/circle
            drawCircle(
                color = goldColor,
                radius = r - 1.5f,
                style = Stroke(width = 1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Inner ring
            drawCircle(
                color = innerColor.copy(alpha = 0.8f),
                radius = r - 4f,
                style = Stroke(width = 0.9f)
            )
        }
        Text(
            text = QuranData.toArabicDigits(ayahNumber),
            fontFamily = AmiriFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isNightMode) Color.White else Color(0xFF2C2416),
            textAlign = TextAlign.Center
        )
    }
}

// Side Hizb and Juz ornamental badge (shown on page margin in screenshot 1)
@Composable
fun HizbOrnamentalBadge(
    juzNumber: Int,
    hizbNumber: Int,
    modifier: Modifier = Modifier,
    isNightMode: Boolean = false
) {
    val gold = if (isNightMode) QuranGold else QuranAyahGold

    Box(
        modifier = modifier.size(44.dp, 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            val path = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(w, h * 0.25f)
                lineTo(w, h * 0.75f)
                lineTo(w / 2f, h)
                lineTo(0f, h * 0.75f)
                lineTo(0f, h * 0.25f)
                close()
            }
            drawPath(
                path = path,
                color = gold,
                style = Stroke(width = 1.5f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = "الجزء ${QuranData.toArabicDigits(juzNumber)}",
                fontFamily = AmiriFamily,
                fontSize = 8.sp,
                color = if (isNightMode) Color.LightGray else Color(0xFF5A4D3B),
                lineHeight = 10.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "الحزب ${QuranData.toArabicDigits(hizbNumber)}",
                fontFamily = AmiriFamily,
                fontSize = 8.sp,
                color = if (isNightMode) Color.LightGray else Color(0xFF5A4D3B),
                lineHeight = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Bottom diamond scrubber item (as shown in Screenshot 1)
@Composable
fun DiamondScrubberItem(
    isSelected: Boolean,
    label: String? = null,
    sizeDp: Dp = if (isSelected) 40.dp else 30.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(sizeDp + 8.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Rotated square gives diamond (rhombus)
        Box(
            modifier = Modifier
                .size(sizeDp)
                .rotate(45f)
                .background(
                    if (isSelected) Color(0xFFFAF6EB) else Color(0xFFFAF6EB).copy(alpha = 0.82f)
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) QuranGold else Color(0xFFC7AF80)
                )
        )

        // Text un-rotated so it is upright, showing page number
        if (label != null) {
            Text(
                text = label,
                fontFamily = AmiriFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = if (isSelected) 14.sp else 11.sp,
                color = if (isSelected) Color(0xFF1E2B1A) else Color(0xFF3B4D36),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Surah Banner inside page header
@Composable
fun SurahHeaderBanner(
    surahName: String,
    modifier: Modifier = Modifier,
    isNightMode: Boolean = false
) {
    Box(
        modifier = modifier
            .border(
                1.5.dp,
                if (isNightMode) QuranGold else Color(0xFFC2AB77)
            )
            .background(
                if (isNightMode) Color(0xFF232A20) else Color(0xFFF3ECE0)
            )
            .padding(horizontal = 24.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = surahName,
            fontFamily = AmiriQuranFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (isNightMode) Color.White else Color(0xFF332717),
            textAlign = TextAlign.Center
        )
    }
}
