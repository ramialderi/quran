package com.example.data

import android.content.Context
import org.json.JSONObject

data class QuranVerse(
    val surah: Int,
    val surahName: String,
    val ayah: Int,
    val number: Int,
    val juz: Int,
    val text: String
)

object QuranTextRepository {
    private val pageCache = mutableMapOf<Int, List<QuranVerse>>()
    private var rawJsonObject: JSONObject? = null
    private var isLoaded = false

    fun initialize(context: Context) {
        if (isLoaded) return
        try {
            val jsonString = context.assets.open("quran_pages.json").bufferedReader().use { it.readText() }
            rawJsonObject = JSONObject(jsonString)
            isLoaded = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPageVerses(context: Context, pageNumber: Int): List<QuranVerse> {
        val safePage = pageNumber.coerceIn(1, 604)
        pageCache[safePage]?.let { return it }

        if (!isLoaded) {
            initialize(context)
        }

        val json = rawJsonObject ?: return emptyList()
        val pageKey = safePage.toString()
        if (!json.has(pageKey)) return emptyList()

        val jsonArray = json.getJSONArray(pageKey)
        val list = ArrayList<QuranVerse>(jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                QuranVerse(
                    surah = obj.getInt("surah"),
                    surahName = obj.getString("surahName"),
                    ayah = obj.getInt("ayah"),
                    number = obj.getInt("number"),
                    juz = obj.getInt("juz"),
                    text = obj.getString("text")
                )
            )
        }
        pageCache[safePage] = list
        return list
    }
}
