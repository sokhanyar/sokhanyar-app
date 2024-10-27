package ir.saltech.myapps.stutter.ui.view.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import ir.saltech.ai.client.generativeai.GenerativeModel
import ir.saltech.ai.client.generativeai.type.content
import ir.saltech.ai.client.generativeai.type.generationConfig
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.BaseApplication.Constants.MOTIVATION_WITH_SPEECH
import ir.saltech.myapps.stutter.BuildConfig
import ir.saltech.myapps.stutter.dto.api.ApiCallback
import ir.saltech.myapps.stutter.dto.api.ApiClient
import ir.saltech.myapps.stutter.dto.api.call
import ir.saltech.myapps.stutter.dto.model.CallsCount
import ir.saltech.myapps.stutter.dto.model.Credit
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.DailyReports
import ir.saltech.myapps.stutter.dto.model.VoicesProperties
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.WeeklyReports
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.util.asToken
import ir.saltech.myapps.stutter.util.dataStore
import ir.saltech.myapps.stutter.util.fromJson
import ir.saltech.myapps.stutter.util.get
import ir.saltech.myapps.stutter.util.getLastDailyReports
import ir.saltech.myapps.stutter.util.response
import ir.saltech.myapps.stutter.util.set
import ir.saltech.myapps.stutter.util.toJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.roundToInt

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    private val openai = OpenAI(
        token = BaseApplication.Constants.API_KEY,
        host = OpenAIHost("${BaseApplication.Constants.BASE_URL}/v1/")
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    private var sentence: MutableList<ChatChunk> = mutableStateListOf()
        set(value) {
            _uiState.update { it.copy(sentence = value) }
        }
    private var advice: String? = ""
        set(value) {
            _uiState.update { it.copy(advice = value) }
        }
    var credit: Credit? = null
        set(value) {
            _uiState.update { it.copy(credit = value) }
        }
    private var speech: ByteArray? = null
        set(value) {
            _uiState.update { it.copy(speech = value) }
        }
    private val _speechOutput = MutableStateFlow<FileInputStream?>(null)
    val speechOutput: StateFlow<FileInputStream?> = _speechOutput

    var dailyReport: DailyReport = DailyReport()
        set(value) {
            _uiState.update { it.copy(dailyReport = value) }
        }
    var weeklyReport: WeeklyReport = WeeklyReport()
        set(value) {
            _uiState.update { it.copy(weeklyReport = value) }
        }
    private var dailyReports: DailyReports = DailyReports()
        set(value) {
            _uiState.update { it.copy(dailyReports = value) }
        }
    private var weeklyReports: WeeklyReports = WeeklyReports()
        set(value) {
            _uiState.update { it.copy(weeklyReports = value) }
        }

    fun generateNewSentence() {
        viewModelScope.launch {
            try {
                openai.chatCompletions(
                    request = ChatCompletionRequest(
                        model = ModelId("gpt-3.5-turbo"),
                        messages = listOf(
                            ChatMessage.System(content = "You are a motivator. A person who stutters and is disappointed with the treatment. You should write different and beautiful motivational sentences in Persian daily for this patient."),
                            ChatMessage.User(content = "Say a different motivation sentence for today.")
                        ),
                        temperature = 1.0,
                    )
                ).collect {
                    sentence = sentence.apply {
                        addAll(it.choices)
                        Log.d("TAG", "Response Collected: ${it.choices}")
                    }
                }
                if (sentence.isNotEmpty() && sentence.last().delta?.content == null && MOTIVATION_WITH_SPEECH) {
                    Log.i("TAG", "Requesting speech audio...")
                    speech = openai.speech(
                        request = SpeechRequest(
                            model = ModelId("tts-1"),
                            input = sentence.response(),
                            voice = Voice.Alloy
                        )
                    )
                    _speechOutput.value = saveSpeechFile()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun <T> generateAdvice(reports: List<T>) {
        viewModelScope.launch {
            val model = GenerativeModel(
                "gemini-1.5-flash-exp-0827",
                BuildConfig.geminiApiKey,
                generationConfig = generationConfig {
                    temperature = 0.8f
                    topK = 64
                    topP = 0.95f
                    maxOutputTokens = 8192
                    responseMimeType = "text/plain"
                },
                systemInstruction = content {
                    text(
                        """
                            My name is <PATIENT_NAME>, I am a stuttered person who should send a daily report to his speech therapist. this report has some parameters.
                            You should analyze and compare previous and today's daily reports and advise me.
                            The Definition of these parameters is:
                            میزان تمرین یعنی مطالعه کتاب، با رعایت شیوه ای که گفتار درمان گفته است و در روز باید حداقل 10 دقیقه و بهتر است بین 30 تا 45 دقیقه باشد.
                            رعایت شیوه یعنی چقدر شیوه کشیده گویی کلمات را در موقعیت های گفتاری، در بیانم استفاده می کنم و معمولاً در هر مکانی باید وجود داشته باشد (به جز مدرسه (دانشگاه) که از 1 مهر تا 31 خرداد باز هستند) و هر چه بیشتر بهتر؛
                            همچنین تعداد حساسیت زدایی یعنی اینکه چه تعداد در روز، با افراد مختلف در مورد لکنت و افشاسازی آن بحث و یا انجام لکنت عمدی در حین صحبت (برای کنترل استرس، قبل از صحبت کردن). که هر چه بیشتر باشد بهتر است. 
                            تعداد لکنت عمدی یعنی اینکه چه زمان هایی وقتی می دانستم زبانم قفل نمی کند و لکنت نمی کند، عمداً برای اینکه به ترسم غلبه کنم، لکنت کرده ام. این مورد معمولا از 4 تا 9 میتواند باشد. 
                            تعداد تشخیص اجتناب یعنی اینکه در چه زمان هایی وقتی داشتم صحبت می کردم، حس کردم که الان، کلمه ای که می خواهم بیان کنم، قرار است روی آن لکنت کنم و اون کلمه رو با کلمه ای که حس می کنم لکنت نمی کنم، عوض نکردم و با وجود اینکه می دونستم قرار است که لکنت کنم، بیان کردم. این مورد میتونه تا 20 عدد هم باشه.
                             تماس همیاری یعنی یکسری از تماس هایی که دو فرد دارای لکنت باهم برقرار می کنند تا با هم به صورت تماس تصویری باهم تمرین کنند و شیوه های خود را در طول تمرین انجام دهند. 
                            تماس گروهی یعنی چند نفر دارای لکنت هر یکشنبه و چهارشنبه با هم تماس تصویری برقرار می کنند و به اجرای شیوه ها در طی سؤالاتی که از آنها پرسیده می شود، می پردازند. اگر امروز، یک شنبه یا چهارشنبه بود، باید تعداد تماس گروهی برابر 1 باشه. 
                            تعداد چالش یعنی اینکه داخل گروه درمانی، چالشی ارسال میشه که درمانجو ها موظف هستند، نظر و برداشت خودشون رو در مورد اون چالش ها بیان کنند. این مورد باید 1 عدد باشه ولی ممکنه درمانجو بخواهد چالش های قبلی که ارسال نکرده است را ارسال کند که حداکثر می تواند 3 چالش در روز ارسال کند. 
                            مجموع کنفرانس بر دقیقه یعنی اینکه چقدر امروز در مورد موضوع های مختلف صحبت کردم و ویس ارسال کرده¬ام. این مورد، معمولاً بهتر است بین 4، 5 تا 10 دقیقه باشد. بیشتر هم بود بهتره. 
                            و در نهایت رضایت از خودم یعنی اینکه چقدر امروز از نحوه صحبت کردنم و بدون لکنت بودن و انجام شیوه های گفتار درمانی در طول روز، رضایت داشتم و از 0 تا 10 به خودم نمره بدهم.
                            You should respond to me by following these parameters:
                            باید گزارش ها رو تحلیل کنی و بر اساس آنچه که خودت می دانی و اینجا بهت گفته شده، به من بازخورد بدهی. اگر به بازخورد های سابق (اگر وجود داشت) عمل نکردم، به من تذکر بدهی . لطفاً فرض کن که یک گفتار درمان (speech therapist) هستی و مثل او با من حرف بزن.  ببین که او چه کار می کند و مثل او با من رفتار کن. و اسم تو "درمانجو یار" هست. یعنی دستیار درمانجو (کسی که در حال درمان لکنت است، یعنی من!)؛ تو هوش مصنوعی داخل اپلیکیشنی تحت این عنوان هستی و وظیفه مشاوره دادن به درمانجو رو وقتی که گزارش روزانه اش رو ارسال می کنه، هستی.
                        """.trimIndent()
                    )
                },
            )

            val generatedContent = model.generateContent(
                content("user") {
                    text(
                        "سلام برای گزارش امروز یه بررسی داشته باش لطفاً:\nگزارش امروز:\n📝\"فرم گزارش روزانه\"\n◾️تاریخ: ۱۵ شهریور\n◾️نام: محمد صالح\n☑️مدت زمان تمرین: ۲۰ دقیقه\n☑️مدت زمان اجرای شیوه درانواع محیط ها👇\nبین ۵ تا ۱۵ دقیقه👈۱ \nبین ۱۵ تا ۳۰ دقیقه👈۲ \nبین ۳۰ تا۶۰ دقیقه👈۳\nبیشتر از یک ساعت👈۴\n خانه: ۳\n مدرسه(دانشگاه): \n غریبه ها:\n فامیل و آشنا: ۱\n☑️تعداد حساسیت زدایی:\n☑️تعداد لکنت عمدی: ۵ \n☑️تعداد تشخیص اجتناب: ۷\n☑️تعدادتماس همیاری: \n☑️تعدادتماس گروهی: \n☑️تعدادچالش: ۱\n☑️کنفرانس بر حسب دقیقه: ۸\n☑️رضایت از خودم(۱ تا ۱۰) : ۸\nتوضیحات:\nگزارش دیروز:\n📝\"فرم گزارش روزانه\"\n◾️نام : محمد صالح\n◼تاریخ:  ۱۴ شهریور \n☑️مدت زمان تمرین : ۱۵ دقیقه\n☑️مدت زمان اجرای شیوه درانواع محیط ها👇\nبین ۵ تا ۱۵ دقیقه👈۱ \nبین ۱۵ تا ۳۰ دقیقه👈۲ \nبین ۳۰ تا ۶۰ دقیقه👈۳\nبیشتر از یک ساعت👈۴\n خانه : ۲\n مدرسه(دانشگاه) : _\n غریبه ها : _\n فامیل و آشنا : ۱\n☑️تعداد حساسیت زدایی : ۱\n☑️تعداد لکنت عمدی : ۴\n☑️تعداد تشخیص اجتناب : ۷\n☑️تعدادتماس همیاری : _\n☑️تعدادتماس گروهی : ۱\n☑️تعدادچالش : _\n☑️کنفرانس بر حسب دقیقه : _\n☑️رضایت از خودم(۱ تا ۱۰) : ۷\nتوضیحات : _"
                    )
                }
            )
            advice = generatedContent.text
        }
    }

    fun loadVoicesProperties() {
        viewModelScope.launch {
            if (_uiState.value.dailyReport.voicesProperties == VoicesProperties()) {
                dailyReport =
                    _uiState.value.dailyReport.copy(voicesProperties = getVoicesProperties())
            }
        }
    }

    private fun getVoicesProperties(): VoicesProperties {
        val recordings: MutableList<Pair<String, Int>> = mutableListOf()
        val recordingFolder = Environment.getExternalStoragePublicDirectory("Recordings")
        recordingFolder.walkTopDown().onEnter { file: File ->
            file.lastModified() >= Clock.System.todayIn(TimeZone.currentSystemDefault())
                .toEpochDays() * 86_400_000.toLong()
        }.filter { file: File ->
            file.lastModified() >= Clock.System.todayIn(TimeZone.currentSystemDefault())
                .toEpochDays() * 86_400_000.toLong()
        }.forEach {
            Log.i("TAG", "preparing file: $it")
            if (it.isFile) recordings.add(
                it.name.split(".").first() to (MediaPlayer.create(
                    context, it.toUri()
                ).duration.toFloat() / 60_000f).roundToInt()
            )
        }
        val challengesCount = recordings.count { it.first.startsWith("چالش") }
        val conferences =
            recordings.filter { it.first.startsWith("کنفرانس") || it.first.startsWith("گزارش") }
        val sumOfConferencesDuration = conferences.sumOf { it.second }
        Log.i(
            "TAG",
            "result: recordings: $recordings , challenges: $challengesCount , conferences: ${conferences.count()} , sumOfConferencesDuration: $sumOfConferencesDuration"
        )
        return VoicesProperties(challengesCount = challengesCount.takeIf { it > 0 },
            sumOfConferencesDuration = sumOfConferencesDuration.takeIf { it > 0 })
    }

    fun checkCredits(callback: ApiCallback<Credit>) {
        ApiClient.avalAi.getCredit(BaseApplication.Constants.API_KEY.asToken()).call(callback)
    }

    fun getDefaultWeeklyReport(): WeeklyReport? {
        val res =
            if (_uiState.value.dailyReports != null && _uiState.value.dailyReports?.list?.isNotEmpty() == true) {
                val lastDailyReports: List<DailyReport> =
                    _uiState.value.dailyReports?.getLastDailyReports() ?: return null
                _uiState.value.weeklyReport.copy(name = _uiState.value.dailyReport.name,
                    voicesProperties = lastDailyReports.let { lastReports ->
                        VoicesProperties(lastReports.sumOf { lastDailyReport: DailyReport ->
                            lastDailyReport.voicesProperties.challengesCount ?: 0
                        }.takeIf { it > 0 }, lastReports.count { lastDailyReport: DailyReport ->
                            lastDailyReport.voicesProperties.sumOfConferencesDuration != 0
                        }.takeIf { it > 0 }, lastReports.sumOf { lastDailyReport: DailyReport ->
                            lastDailyReport.voicesProperties.sumOfConferencesDuration ?: 0
                        }.takeIf { it > 0 })
                    },
                    practiceDays = lastDailyReports.count { (it.practiceTime ?: 0) >= 3 }
                        .takeIf { it > 0 },
                    callsCount = lastDailyReports.let { lastReports ->
                        CallsCount(lastReports.sumOf { lastDailyReport: DailyReport ->
                            lastDailyReport.callsCount.groupCallsCount ?: 0
                        }.takeIf { it > 0 }, lastReports.sumOf { lastDailyReport: DailyReport ->
                            lastDailyReport.callsCount.adultSupportCallsCount ?: 0
                        }.takeIf { it > 0 }, lastReports.sumOf { lastDailyReport: DailyReport ->
                            lastDailyReport.callsCount.teenSupportCallsCount ?: 0
                        }.takeIf { it > 0 })
                    },
                    desensitizationCount = lastDailyReports.sumOf { lastReports ->
                        lastReports.desensitizationCount ?: 0
                    }.takeIf { it > 0 },
                    dailyReportsCount = lastDailyReports.count().takeIf { it > 0 })
            } else null
        Log.i("TAG", "Default Weekly Report loaded: $res")
        return res
    }


    private fun saveSpeechFile(): FileInputStream {
        val speechFile = File.createTempFile("speech", "mp3", context.cacheDir)
        speechFile.deleteOnExit()
        val fos = FileOutputStream(speechFile)
        fos.write(_uiState.value.speech)
        fos.close()
        return FileInputStream(speechFile)
    }


    fun loadDailyReports() {
        dailyReports =
            fromJson<DailyReports>(context.dataStore[BaseApplication.Key.DailyReports] ?: "")
                ?: DailyReports()
        dailyReport = _uiState.value.dailyReport.copy(
            name = _uiState.value.dailyReports?.list?.lastOrNull()?.name,
            date = Clock.System.now().toEpochMilliseconds()
        )
        Log.i("TAG", "Latest daily reports fetched: ${_uiState.value.dailyReports}")
    }

    fun saveDailyReport(): Boolean {
        val res = _uiState.value.dailyReports?.list?.add(_uiState.value.dailyReport)
        saveDailyReports()
        return res == true
    }

    private fun saveDailyReports() {
        Log.i("TAG", "saving daily reports json: ${toJson(_uiState.value.dailyReports)}")
        context.dataStore[BaseApplication.Key.DailyReports] =
            toJson(_uiState.value.dailyReports) ?: ""
    }


    fun loadWeeklyReports() {
        weeklyReports =
            fromJson<WeeklyReports>(context.dataStore[BaseApplication.Key.WeeklyReports] ?: "")
                ?: WeeklyReports()
        weeklyReport = _uiState.value.weeklyReport.copy(
            name = _uiState.value.weeklyReports?.list?.lastOrNull()?.name,
            date = Clock.System.now().toEpochMilliseconds()
        )
        Log.i("TAG", "Latest weekly reports fetched: ${_uiState.value.weeklyReports}")
    }

    fun saveWeeklyReport(): Boolean {
        val res = _uiState.value.weeklyReports?.list?.add(_uiState.value.weeklyReport)
        saveWeeklyReports()
        return res == true
    }

    private fun saveWeeklyReports() {
        Log.i("TAG", "saving weekly reports json: ${toJson(_uiState.value.weeklyReports)}")
        context.dataStore[BaseApplication.Key.WeeklyReports] =
            toJson(_uiState.value.weeklyReports) ?: ""
    }
}