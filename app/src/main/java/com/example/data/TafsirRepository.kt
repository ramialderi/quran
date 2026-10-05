package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class TafsirResult(
    val surahId: Int,
    val ayahNumber: Int,
    val surahName: String,
    val tafsirName: String,
    val text: String,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TafsirRepository {
    private val cache = mutableMapOf<String, String>()

    suspend fun getTafsir(surahId: Int, ayahNumber: Int): TafsirResult = withContext(Dispatchers.IO) {
        val cacheKey = "$surahId:$ayahNumber"
        val surah = QuranData.surahs.find { it.id == surahId }
        val surahName = surah?.fullName ?: "سورة"

        // Check in-memory cache first
        if (cache.containsKey(cacheKey)) {
            return@withContext TafsirResult(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahName = surahName,
                tafsirName = "التفسير الميسر (مجمع الملك فهد)",
                text = cache[cacheKey]!!
            )
        }

        try {
            val endpoint = "https://api.quran.com/api/v4/tafsirs/16/by_ayah/$surahId:$ayahNumber"
            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                val tafsirObj = json.optJSONObject("tafsir")
                val rawText = tafsirObj?.optString("text") ?: ""

                // Clean HTML tags and decode entities
                val cleanText = rawText
                    .replace(Regex("<[^>]*>"), "")
                    .replace("&nbsp;", " ")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'")
                    .replace("&amp;", "&")
                    .trim()

                if (cleanText.isNotEmpty()) {
                    cache[cacheKey] = cleanText
                    return@withContext TafsirResult(
                        surahId = surahId,
                        ayahNumber = ayahNumber,
                        surahName = surahName,
                        tafsirName = "التفسير الميسر (مجمع الملك فهد)",
                        text = cleanText
                    )
                }
            }

            return@withContext TafsirResult(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahName = surahName,
                tafsirName = "التفسير الميسر",
                text = "",
                error = "تعذر تحميل التفسير، يرجى التأكد من الاتصال بالإنترنت."
            )
        } catch (e: Exception) {
            return@withContext TafsirResult(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahName = surahName,
                tafsirName = "التفسير الميسر",
                text = "",
                error = "تعذر جلب التفسير حالياً: ${e.localizedMessage ?: "خطأ في الشبكة"}"
            )
        }
    }
}
