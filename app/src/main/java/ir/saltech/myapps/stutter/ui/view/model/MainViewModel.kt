package ir.saltech.myapps.stutter.ui.view.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import ir.saltech.ai.client.generativeai.GenerativeModel
import ir.saltech.ai.client.generativeai.type.content
import ir.saltech.ai.client.generativeai.type.generationConfig
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.dto.model.CallsCount
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.DailyReports
import ir.saltech.myapps.stutter.dto.model.VoicesProperties
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.WeeklyReports
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.util.dataStore
import ir.saltech.myapps.stutter.util.fromJson
import ir.saltech.myapps.stutter.util.get
import ir.saltech.myapps.stutter.util.getCommandBasedOnTime
import ir.saltech.myapps.stutter.util.getGreetingBasedOnTime
import ir.saltech.myapps.stutter.util.getLastDailyReports
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
    private var sentence: String = getGreetingBasedOnTime()
        set(value) {
            _uiState.update { it.copy(sentence = value) }
        }
    private var advice: String? = ""
        set(value) {
            _uiState.update { it.copy(advice = value) }
        }
    var page: BaseApplication.Page = BaseApplication.Page.Home
        set(value) {
            _uiState.update { it.copy(page = value) }
        }
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


    fun <T> generateAdvice(reports: List<T>) {
        viewModelScope.launch {
            val model = GenerativeModel(
                modelName = BaseApplication.Ai.Gemini.Models.Flash,
                BaseApplication.Ai.Gemini.apiKeys[0],
                systemInstruction = content {
                    text(BaseApplication.Ai.Gemini.systemInstruction)
                },
                generationConfig = generationConfig {
                    temperature = 0.8f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 1024
                    responseMimeType = "text/plain"
                }
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

    fun generateNewMotivationText() {
        viewModelScope.launch {
            try {
                val model = GenerativeModel(
                    modelName = BaseApplication.Ai.Gemini.Models.Flash,
                    BaseApplication.Ai.Gemini.apiKeys[0],
                    systemInstruction = content {
                        text(
                            """\n\n
                            Some example of stuttering and activity motivation passages in Persian:\n
            Example 1:
            سلام صبح قشنگ پاییزیتون بخیر وشادی🌹\n
            \n
            زندگی را می گویم:🌸 \n
            بخواهی از آن لذت ببری، \n
  همه چیزش لذت بردنی است \n          
  و اگر بخواهی از آن رنج ببری \n          
  همه چیزش رنج بردنی است، \n          
  کلید لذت و رنج دست توست !🌸🍂 \n          
            Example 2:
            سلام صبحتون بخیر🪴\n
            \n
            پرسودترین معامله زندگی \n
حال خوب را جایگزين \n            
حال بدکردن است. 😇 \n            
همـین✌️ \n            
حال دلتون خوب، وجودتون \n            
سبز و سلامت، \n            
زندگیتون غرق در خوشبختی \n            
            \n
            \n🍁🍂🍁🍂
            Example 3:
            \n ♥️💫سلام😊✋ و صد سلام
🤍💫مــهــرتـون بــے پایان... \n            
            \n
            ♥️💫روزتون پر از سلامتی \n
♥️💫سرشار از مهر و دوستی \n            
🤍💫موفقیت و لطف خدای مهربان \n            
            \n
♥️💫بـا آرزوی یک روز عــالـی \n            
🤍💫صــبــحــتــون پــر از اتفاقات خوب و به دور از لکنت! 😇 \n            
            Example 4:
            \n ♥️🍃
            \n
❣رؤیاهاتون که کوچک و محدود شد، \n            
زندگیتون محدود میشه؛ 😢 \n            
زندگیتون که محدود شد، \n            
به کم قانع میشید! 😓 \n            
به کم که قانع شدید، \n            
دیگه هیچ اتفاق جدیدی توی زندگیتون نمیوفته!! 😞✋ \n            
            \n
رؤیاهای بزرگ داشته باشید.😉✌️ \n            
صبحتون بخیر! \n            
            \n
            \n ❤️🕊 ◕‿◕
            Example 5:
            واقعی ترین خوشی آدما\n
            اون لحظست که\n
            با خیال راحت میگی\n
            باورم نمیشه بلاخره شد :)\n
            \n
                        """.trimIndent()
                        )
                    },
                    generationConfig = generationConfig {
                        temperature = 1.7f
                        topK = 40
                        topP = 0.95f
                        maxOutputTokens = 128
                        responseMimeType = "text/plain"
                    }
                )
                val generatedContent = model.generateContent(
                    " ${getCommandBasedOnTime()} یک جمله انگیزشی به من بگو.\\n\n" +
                            "فقط جمله انگیزشی ات رو نشون بده. جلمه انگیزشی باید فقط 1 خط باشه و به همراه ایموجی جذاب باشه.\\n\n"
                )
                sentence = generatedContent.text?.trim() ?: sentence
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}