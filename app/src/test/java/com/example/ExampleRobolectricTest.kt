package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.QuranData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("القرآن الكريم", appName)
    }

    @Test
    fun `verify quran data integrity`() {
        assertEquals(114, QuranData.surahs.size)
        val fatiha = QuranData.surahs.first()
        assertEquals("الفاتحة", fatiha.name)
        assertEquals(1, fatiha.startPage)

        val page62 = QuranData.getPage(62)
        assertNotNull(page62)
        assertEquals(4, page62.juzNumber)
        assertEquals(62, page62.pageNumber)
    }
}
