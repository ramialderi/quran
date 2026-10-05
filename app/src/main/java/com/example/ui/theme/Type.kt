package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Authentic Traditional Quranic Calligraphy Font (Scheherazade New)
val QuranCalligraphyFamily = FontFamily(
    Font(R.font.scheherazade_new, FontWeight.Normal)
)

val AmiriQuranFamily = FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal)
)

val AmiriFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold)
)

val QuranTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 44.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 34.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = QuranCalligraphyFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 46.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
