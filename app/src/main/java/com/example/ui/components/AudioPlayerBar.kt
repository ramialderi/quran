package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayerState
import com.example.data.QuranData
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranTextDark

@Composable
fun AudioPlayerBar(
    playerState: AudioPlayerState,
    isNightMode: Boolean = false,
    onTogglePlayPause: () -> Unit,
    onPreviousAyah: () -> Unit,
    onNextAyah: () -> Unit,
    onOpenReciterSelection: () -> Unit,
    onOpenPlaylists: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onClosePlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = playerState.isPlayerVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        val surah = QuranData.surahs.find { it.id == playerState.currentSurah }
        val surahName = surah?.fullName ?: "سورة"

        // Ultra-compact, slim single-row bottom audio dock
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isNightMode) Color(0xFF1C221A) else Color(0xFFFBF6EE)
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            border = BorderStroke(1.dp, if (isNightMode) Color(0xFF2E382C) else QuranGold.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .testTag("audio_player_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Reciter Chip + Surah/Ayah Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Clickable Reciter Name chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isNightMode) Color(0xFF273124) else Color(0xFFEDE2CD))
                            .clickable(onClick = onOpenReciterSelection)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                            .testTag("choose_reciter_chip")
                    ) {
                        Text(
                            text = playerState.currentReciter.name.split(" ").take(2).joinToString(" "),
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isNightMode) Color(0xFFE0D8C8) else QuranTextDark
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "$surahName : ${QuranData.toArabicDigits(playerState.currentAyah)}",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isNightMode) Color(0xFFE2C88E) else QuranGoldBanner
                    )
                }

                // Middle: Playback Controls (Prev, Play/Pause, Next)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Next Ayah
                    IconButton(
                        onClick = onNextAyah,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("next_ayah_audio_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الآية التالية",
                            tint = if (isNightMode) Color.White else QuranTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Play / Pause / Buffering Button (Compact 34dp)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(QuranGold)
                            .clickable(onClick = onTogglePlayPause)
                            .testTag("play_pause_audio_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playerState.isBuffering) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "إيقاف مؤقت" else "تشغيل التلاوة",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Previous Ayah
                    IconButton(
                        onClick = onPreviousAyah,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("prev_ayah_audio_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "الآية السابقة",
                            tint = if (isNightMode) Color.White else QuranTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Right: Playlists & Close
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Add current recitation to Playlist
                    IconButton(
                        onClick = onAddToPlaylist,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("add_to_playlist_bar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "إضافة لقائمة تشغيل",
                            tint = QuranGoldBanner,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Open Playlists Library
                    IconButton(
                        onClick = onOpenPlaylists,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("open_playlists_bar_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "قوائم التشغيل",
                            tint = if (isNightMode) Color(0xFFC7AF80) else Color(0xFF7D725F),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Close Audio Player
                    IconButton(
                        onClick = onClosePlayer,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_audio_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق المشغل",
                            tint = Color.Gray,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
