package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class Reciter(
    val id: String,
    val name: String,
    val subName: String,
    val folder: String
)

object RecitersData {
    val defaultReciter = Reciter(
        id = "alafasy",
        name = "مشاري بن راشد العفاسي",
        subName = "رواية حفص عن عاصم",
        folder = "Alafasy_128kbps"
    )

    val reciters = listOf(
        defaultReciter,
        Reciter(
            id = "abdulbasit",
            name = "عبد الباسط عبد الصمد",
            subName = "المصحف المرتل",
            folder = "Abdul_Basit_Murattal_192kbps"
        ),
        Reciter(
            id = "husary",
            name = "محمود خليل الحصري",
            subName = "المصحف المرتل",
            folder = "Husary_128kbps"
        ),
        Reciter(
            id = "minshawy",
            name = "محمد صديق المنشاوي",
            subName = "المصحف المرتل",
            folder = "Minshawy_Murattal_128kbps"
        ),
        Reciter(
            id = "shatri",
            name = "أبو بكر الشاطري",
            subName = "رواية حفص عن عاصم",
            folder = "Abu_Bakr_Ash-Shaatree_128kbps"
        ),
        Reciter(
            id = "ghamadi",
            name = "سعد الغامدي",
            subName = "رواية حفص عن عاصم",
            folder = "Ghamadi_40kbps"
        )
    )
}

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurah: Int = 1,
    val currentAyah: Int = 1,
    val currentReciter: Reciter = RecitersData.defaultReciter,
    val isPlayerVisible: Boolean = false,
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private var onCompletionCallback: (() -> Unit)? = null

    fun playAyah(
        surah: Int,
        ayah: Int,
        reciter: Reciter = _playerState.value.currentReciter,
        onCompletion: (() -> Unit)? = null
    ) {
        onCompletionCallback = onCompletion
        val url = String.format(
            Locale.US,
            "https://everyayah.com/data/%s/%03d%03d.mp3",
            reciter.folder,
            surah,
            ayah
        )

        _playerState.value = _playerState.value.copy(
            isBuffering = true,
            isPlaying = false,
            currentSurah = surah,
            currentAyah = ayah,
            currentReciter = reciter,
            isPlayerVisible = true,
            errorMessage = null
        )

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = true,
                        isBuffering = false
                    )
                }
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(isPlaying = false)
                    onCompletionCallback?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = "تعذر تشغيل التلاوة. يرجى التحقق من الاتصال بالإنترنت."
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                isBuffering = false,
                errorMessage = "خطأ أثناء تشغيل الملف الصوتي"
            )
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            } else {
                mp.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
            }
        } else {
            // Re-trigger playback of current ayah
            val state = _playerState.value
            playAyah(state.currentSurah, state.currentAyah, state.currentReciter, onCompletionCallback)
        }
    }

    fun setReciter(reciter: Reciter) {
        _playerState.value = _playerState.value.copy(currentReciter = reciter)
        if (_playerState.value.isPlaying || _playerState.value.isBuffering) {
            playAyah(_playerState.value.currentSurah, _playerState.value.currentAyah, reciter, onCompletionCallback)
        }
    }

    fun stopAndHide() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isBuffering = false,
            isPlayerVisible = false
        )
    }

    fun release() {
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
