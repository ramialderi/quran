package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.Reciter
import com.example.data.PlaylistItem
import com.example.data.QuranData
import com.example.data.RecitationPlaylist
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.QuranGold
import com.example.ui.theme.QuranGoldBanner
import com.example.ui.theme.QuranIndexBackground
import com.example.ui.theme.QuranTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsSheet(
    isOpen: Boolean,
    playlists: List<RecitationPlaylist>,
    selectedPlaylist: RecitationPlaylist?,
    playlistItems: List<PlaylistItem>,
    onSelectPlaylist: (RecitationPlaylist) -> Unit,
    onBackToPlaylists: () -> Unit,
    onCreatePlaylist: (name: String, description: String) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onPlayItem: (PlaylistItem) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isOpen) return

    var isCreatingNew by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = QuranIndexBackground,
        dragHandle = null,
        modifier = Modifier.testTag("playlists_bottom_sheet")
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
                if (selectedPlaylist != null) {
                    IconButton(
                        onClick = onBackToPlaylists,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("playlist_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "العودة للقوائم",
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("close_playlists_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = selectedPlaylist?.name ?: "قوائم التلاوات القرآنية",
                    fontFamily = AmiriFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (selectedPlaylist == null) {
                    IconButton(
                        onClick = { isCreatingNew = true },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .testTag("create_new_playlist_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إنشاء قائمة جديدة",
                            tint = Color.White
                        )
                    }
                }
            }

            // Create Playlist Dialog/Banner
            if (isCreatingNew) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, QuranGold)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "إنشاء قائمة تشغيلية جديدة",
                            fontFamily = AmiriFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = QuranTextDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            label = { Text("اسم القائمة (مثال: تلاوات الفجر)", fontFamily = AmiriFamily) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_playlist_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = QuranGoldBanner,
                                unfocusedBorderColor = Color(0xFFDCCFBB)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPlaylistDesc,
                            onValueChange = { newPlaylistDesc = it },
                            label = { Text("وصف مختصر (اختياري)", fontFamily = AmiriFamily) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = QuranGoldBanner,
                                unfocusedBorderColor = Color(0xFFDCCFBB)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isCreatingNew = false
                                    newPlaylistName = ""
                                    newPlaylistDesc = ""
                                }
                            ) {
                                Text("إلغاء", fontFamily = AmiriFamily)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (newPlaylistName.isNotBlank()) {
                                        onCreatePlaylist(newPlaylistName.trim(), newPlaylistDesc.trim())
                                        isCreatingNew = false
                                        newPlaylistName = ""
                                        newPlaylistDesc = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = QuranGoldBanner),
                                modifier = Modifier.testTag("confirm_create_playlist_button")
                            ) {
                                Text("إنشاء القائمة", fontFamily = AmiriFamily, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Body: View 1 (List of Playlists) or View 2 (Items in Selected Playlist)
            if (selectedPlaylist == null) {
                // Playlists List
                if (playlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد قوائم تشغيل حالياً",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = QuranTextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "أنشئ قائمتك الأولى لحفظ تلاواتك المفضلة من قرائك المختارين",
                                fontFamily = AmiriFamily,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { isCreatingNew = true },
                                colors = ButtonDefaults.buttonColors(containerColor = QuranGoldBanner)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إنشاء قائمة جديدة", fontFamily = AmiriFamily, color = Color.White)
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
                        items(playlists, key = { it.id }) { playlist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { onSelectPlaylist(playlist) }
                                    .testTag("playlist_card_${playlist.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .background(Color(0xFFF3ECE0), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlaylistPlay,
                                                contentDescription = null,
                                                tint = QuranGoldBanner,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = playlist.name,
                                                fontFamily = AmiriFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = QuranTextDark
                                            )
                                            if (playlist.description.isNotBlank()) {
                                                Text(
                                                    text = playlist.description,
                                                    fontFamily = AmiriFamily,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeletePlaylist(playlist.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "حذف القائمة",
                                            tint = Color.Red.copy(alpha = 0.6f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Items in Selected Playlist
                if (playlistItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "القائمة فارغة حالياً",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = QuranTextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "يمكنك إضافة تلاوات إلى هذه القائمة بالضغط على زر القوائم (+) في شريط المشغل الصوتي أثناء الاستماع لأي سورة وقارئ.",
                                fontFamily = AmiriFamily,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
                        items(playlistItems, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Play button
                                        IconButton(
                                            onClick = { onPlayItem(item) },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(QuranGoldBanner, CircleShape)
                                                .testTag("play_playlist_item_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "تشغيل",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "${item.surahName} • آية ${QuranData.toArabicDigits(item.ayahNumber)}",
                                                fontFamily = AmiriFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = QuranTextDark
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Mic,
                                                    contentDescription = null,
                                                    tint = QuranGoldBanner,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = item.reciterName,
                                                    fontFamily = AmiriFamily,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF5D5343)
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteItem(item.id) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "حذف من القائمة",
                                            tint = Color.Red.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
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

@Composable
fun AddToPlaylistDialog(
    isOpen: Boolean,
    surahId: Int,
    surahName: String,
    ayahNumber: Int,
    reciter: Reciter,
    playlists: List<RecitationPlaylist>,
    onAddToPlaylist: (playlistId: Long) -> Unit,
    onCreateAndAdd: (name: String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var newPlaylistName by remember { mutableStateOf("") }
    var isCreatingNew by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = Color(0xFFFBF6EE),
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_to_playlist_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إضافة إلى قائمة تشغيل",
                        fontFamily = AmiriFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = QuranTextDark
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Recitation Card Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE0)),
                    border = BorderStroke(1.dp, Color(0xFFDCCFBB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = null,
                            tint = QuranGoldBanner,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "$surahName • آية ${QuranData.toArabicDigits(ayahNumber)}",
                                fontFamily = AmiriFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = QuranTextDark
                            )
                            Text(
                                text = "بصوت: ${reciter.name}",
                                fontFamily = AmiriFamily,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isCreatingNew) {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("اسم القائمة الجديدة", fontFamily = AmiriFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = QuranGoldBanner,
                            unfocusedBorderColor = Color(0xFFDCCFBB)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { isCreatingNew = false }) {
                            Text("إلغاء", fontFamily = AmiriFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newPlaylistName.isNotBlank()) {
                                    onCreateAndAdd(newPlaylistName.trim())
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = QuranGoldBanner)
                        ) {
                            Text("إنشاء وإضافة", fontFamily = AmiriFamily, color = Color.White)
                        }
                    }
                } else {
                    Text(
                        text = "اختر القائمة لإضافة التلاوة إليها:",
                        fontFamily = AmiriFamily,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.height(180.dp)) {
                        items(playlists) { pl ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable {
                                        onAddToPlaylist(pl.id)
                                        onDismiss()
                                    }
                                    .testTag("select_playlist_${pl.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2D6C0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = pl.name,
                                        fontFamily = AmiriFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = QuranTextDark
                                    )
                                    Text(
                                        text = "إضافة ＋",
                                        fontFamily = AmiriFamily,
                                        fontSize = 13.sp,
                                        color = QuranGoldBanner
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { isCreatingNew = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_new_in_add_dialog"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, QuranGoldBanner)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = QuranGoldBanner)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("قائمة جديدة...", fontFamily = AmiriFamily, color = QuranGoldBanner)
                    }
                }
            }
        }
    )
}
