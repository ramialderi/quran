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

    private val offlineTafsirMap = mapOf(
        "1:1" to "أبتدئ قراءتي للقرآن باسم الله مستعيناً به، (الله) علم على الرب تبارك وتعالى، المعبود بحق، دون سواه، وهو أخص أسماء الله تعالى. (الرحمن) ذي الرحمة العامة التي وسعت كل شيء، (الرحيم) بالمؤمنين.",
        "1:2" to "الثناء التام على الله بصفاته التي كلها أوصاف كمال، وبنعمه الظاهرة والباطنة، الدينية والدنيوية، وفي ضمنه أَمْرٌ لعباده بأن يثنوا عليه، فهو المستحق له وحده. وهو سبحانه المنشئ للخلق، القائم بأمورهم، المربي لجميع خلقه بنعمه، ولأوليائه بالإيمان والعمل الصالح.",
        "1:3" to "(الرحمن) الذي وسعت رحمته جميع الخلق، (الرحيم) بالمؤمنين، وهما اسمان من أسماء الله تعالى، يتضمنان إثبات صفة الرحمة لله تعالى كما يليق بجلاله.",
        "1:4" to "وهو سبحانه وحده مالك يوم القيامة، وهو يوم الجزاء على الأعمال. وفي تخصيص الملك بيوم الدين؛ لأنه لا يدَّعي أحد هنالك مِلكاً ولا يتكلم أحد إلا بإذنه.",
        "1:5" to "إنا نخصك وحدك بالعبادة، ونستعين بك وحدك في جميع أمورنا، فالأمر كله بيدك، لا يملك منه أحد مثقال ذرة. وفي هذه الآية دليل على أن العبد لا يجوز له أن يصرف شيئاً من أنواع العبادة إلا لله وحده.",
        "1:6" to "دُلَّنا، وأرشدنا، ووفقنا إلى الصراط المستقيم، وثبتنا عليه حتى نلقاك، وهو الإسلام، الذي هو الطريق الواضح الموصل إلى رضوان الله وإلى جنته.",
        "1:7" to "طريق الذين أنعمت عليهم من النبيين والصدِّيقين والشهداء والصالحين، فهم أهل الهداية والاستقامة، ولا تجعلنا ممن سلك طريق المغضوب عليهم، وهم اليهود، ولا طريق الضالين، وهم النصارى ومن سلك سبيلهم.",
        "2:255" to "الله الذي لا إله بحق إلا هو وحده لا شريك له، الحيُّ القيوم القائم على كل شيء، لا تأخذه سِنَةٌ وهي النعاس، ولا نوم. له كل ما في السماوات وما في الأرض خَلْقاً ومُلْكاً وعبيداً، لا يشفع أحد عنده إلا بإذنه، يعلم ما بين أيدي الخلائق وما خلفهم، وسع كرسيه السماوات والأرض، ولا يعجزه ولا يثقله حفظهما، وهو العلي العظيم.",
        "112:1" to "قل -أيها الرسول- لمن سألوك عن ربك: هو الله المتفرد بالألوهية والربوبية والأسماء والصفات، لا شريك له.",
        "112:2" to "الله السيد المستغني عن جميع خلقه، الذي تصمد إليه الخلائق وتقصده في حوائجها ورغائبها.",
        "112:3" to "ليس له ولد، ولم يولد، فليس له والد، لكمال غناه وصمديته وتفرده سبحانه وتعالى.",
        "112:4" to "ولم يكن له مماثلاً ولا مكافئاً أحد من خلقه، لا في أسمائه ولا في صفاته ولا في أفعاله جل وعلا.",
        "113:1" to "قل: أعوذ وأعتصم برب الصبح إذا انشق وأضاء بعد ظلام الليل.",
        "113:2" to "من شر جميع ما خلق الله من الإنس والجن والدواب والحيوانات والآفات.",
        "113:3" to "ومن شر ليل مظلم شديد الظلمة إذا دخل وغطى كل شيء بما فيه من شرور وسباع.",
        "113:4" to "ومن شر الساحرات اللاتي ينفثن في عُقَد الخيوط حين يسحرن بها لإيذاء الخلق.",
        "113:5" to "ومن شر حاسد إذا تمنى زوال النعمة عن غيره وسعى في الإضرار بالمحسود.",
        "114:1" to "قل: أعوذ وأعتصم برب الناس، خالقهم ومدبر أمورهم.",
        "114:2" to "ملك الناس المتصرف في جميع شؤونهم وخلقهم، الغني عنهم.",
        "114:3" to "إله الناس ومعبودهم بحق، الذي لا معبود سواه.",
        "114:4" to "من شر الشيطان الذي يوسوس في قلب الإنسان عند الغفلة، ويختفي ويخنس إذا ذُكر الله.",
        "114:5" to "الذي يبث الشر والشكوك والشهوات في صدور الناس وقلوبهم.",
        "114:6" to "من شياطين الإنس والجن."
    )

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

        // Check offline map for instantaneous response
        if (offlineTafsirMap.containsKey(cacheKey)) {
            val offlineText = offlineTafsirMap[cacheKey]!!
            cache[cacheKey] = offlineText
            return@withContext TafsirResult(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahName = surahName,
                tafsirName = "التفسير الميسر (مجمع الملك فهد)",
                text = offlineText
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
