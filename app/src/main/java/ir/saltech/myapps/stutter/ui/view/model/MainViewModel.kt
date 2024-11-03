package ir.saltech.myapps.stutter.ui.view.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import ir.saltech.myapps.stutter.dto.model.Report
import ir.saltech.myapps.stutter.dto.model.VoicesProperties
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.WeeklyReports
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.util.dataStore
import ir.saltech.myapps.stutter.util.fromJson
import ir.saltech.myapps.stutter.util.get
import ir.saltech.myapps.stutter.util.getGreetingBasedOnTime
import ir.saltech.myapps.stutter.util.getLastDailyReports
import ir.saltech.myapps.stutter.util.set
import ir.saltech.myapps.stutter.util.toDayReportDate
import ir.saltech.myapps.stutter.util.toJalali
import ir.saltech.myapps.stutter.util.toJson
import ir.saltech.myapps.stutter.util.toRegularTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File
import java.util.Date
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
    private var advice: MutableState<String?> = mutableStateOf(null)
        set(value) {
            _uiState.update { it.copy(advice = value) }
        }

    @Deprecated("use activePages instead")
    var page: BaseApplication.Page = BaseApplication.Page.Home
        set(value) {
            _uiState.update { it.copy(page = value) }
        }
    var activePages: MutableList<BaseApplication.Page> =
        mutableStateListOf(BaseApplication.Page.Home)
        set(value) {
            _uiState.update { it.copy(activePages = value) }
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

    // --- AI Section ---

//    fun launchChat(reports: List<Report>?, reportType: BaseApplication.ReportType) {
//        if (reports != null) {
//            viewModelScope.launch {
//                val model = GenerativeModel(
//                    modelName = BaseApplication.Ai.Gemini.Models.Flash,
//                    BaseApplication.Ai.Gemini.apiKeys[0],
//                    systemInstruction = content {
//                        text(BaseApplication.Ai.Gemini.BASE_SYSTEM_INSTRUCTIONS_V1_1)
//                    },
//                    generationConfig = generationConfig {
//                        temperature = 1.3f
//                        topK = 40
//                        topP = 0.95f
//                        maxOutputTokens = 1024
//                        responseMimeType = "text/plain"
//                        frequencyPenalty =0.7f
//                        presencePenalty = 1.2f
//                    }
//                )
//                val chat = model.startChat()
//                val generatedResponse = chat.sendMessage(content {
//                    text(
//                        """سلام گزارش ${reportType.name} امروز رو با توجه به گزارش های قبلی و با دقت تحلیل کن. به طور خلاصه بازخورد بده.\n
//                    گزارش این هفته / امروز:
//                    ${
//                            reports.last().result
//                        }
//                    گزارش های قبلی:
//                    ${
//                            reports.subList(0, reports.lastIndex)
//                        }
//                    """
//                    )
//                })
//                Log.i("TAG", "Advice generated: ${generatedResponse.text}")
//                advice.value = generatedResponse.text
//                _uiState.value.advice.value = generatedResponse.text?.trim()
//            }
//        }
//    }
//
//    fun generateNewMessage(message: String, model: GenerativeModel, history: List<Content>) {
//        viewModelScope.launch {
//            val response = model.startChat(history).sendMessage(content {
//                text(message)
//            })
//        }
//    }

    private fun generateAdvice(reports: List<Report>?, reportType: BaseApplication.ReportType) {
        if (reports != null) {
            viewModelScope.launch {
                val model = GenerativeModel(
                    modelName = BaseApplication.Ai.Gemini.Models.Flash,
                    BaseApplication.Ai.Gemini.apiKeys[0],
                    systemInstruction = content {
                        text(BaseApplication.Ai.Gemini.BASE_SYSTEM_INSTRUCTIONS_V1_1)
                    },
                    generationConfig = generationConfig {
                        temperature = 0.8f
                        topK = 40
                        topP = 0.95f
                        maxOutputTokens = 1024
                        responseMimeType = "text/plain"
                    }
                )
                val chat = model.startChat()
                val generatedResponse = chat.sendMessage(content {
                    text(
                        """سلام گزارش ${reportType.name} امروز رو با توجه به گزارش های قبلی و با دقت تحلیل کن. به طور خلاصه بازخورد بده.\n
                    گزارش این هفته / امروز:
                    ${
                            reports.last().result
                        }
                    گزارش های قبلی:
                    ${
                            reports.subList(0, reports.lastIndex)
                        }
                    """
                    )
                })
                Log.i("TAG", "Advice generated: ${generatedResponse.text}")
                advice.value = generatedResponse.text
                _uiState.value.advice.value = generatedResponse.text?.trim()
            }
        }
    }

    fun generateNewMotivationText() {
        viewModelScope.launch {
            try {
                val model = GenerativeModel(
                    modelName = BaseApplication.Ai.Gemini.Models.Flash,
                    BaseApplication.Ai.Gemini.apiKeys[0],
                    generationConfig = generationConfig {
                        temperature = 1.7f
                        topK = 40
                        topP = 0.95f
                        maxOutputTokens = 100
                        responseMimeType = "text/plain"
                        presencePenalty = 1f
                        frequencyPenalty = 0.4f
                    }
                )
                val generatedContent = model.generateContent(
                    " ${getGreetingBasedOnTime(true)} یک جمله انگیزشی خفن برای درمان لکنت به من بگو.\n" +
                            "فقط جمله انگیزشی ات رو نشون بده. جلمه انگیزشی باید فقط 1 خط باشه و به همراه ایموجی جذاب باشه.\n"
                )
                sentence = generatedContent.text?.trim() ?: sentence
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // --- Process Reports Section ---

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
        val challenges =
            recordings.filter { it.first.startsWith("چالش") }
        val challengesCount = challenges.count()
        val sumOfChallengesDuration = challenges.sumOf { it.second }
        val conferences =
            recordings.filter { it.first.startsWith("کنفرانس") || it.first.startsWith("گزارش") }
        val sumOfConferencesDuration = conferences.sumOf { it.second }
        Log.i(
            "TAG",
            "result: recordings: $recordings , challenges: $challengesCount, sumOfChallengesDuration: $sumOfChallengesDuration , conferences: ${conferences.count()} , sumOfConferencesDuration: $sumOfConferencesDuration"
        )
        return VoicesProperties(challengesCount = challengesCount.takeIf { it > 0 },
            sumOfChallengesDuration = sumOfChallengesDuration.takeIf { it > 0 },
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
                        }.takeIf { it > 0 }, lastReports.sumOf { lastDailyReport: DailyReport ->
                            lastDailyReport.voicesProperties.sumOfChallengesDuration ?: 0
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

    // --- Load and Save Section ---

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
        dailyReport = _uiState.value.dailyReport.copy(result = """
            📝"فرم گزارش روزانه"
            ◾️تاریخ: ${Date(_uiState.value.dailyReport.date!!).toJalali().toDayReportDate()} 
            ◾️نام: ${(_uiState.value.dailyReport.name ?: "").ifEmpty { "ناشناس" }}
            ☑️مدت زمان تمرین: ${_uiState.value.dailyReport.practiceTime?.toRegularTime() ?: "-"}
            ☑️مدت زمان اجرای شیوه در انواع محیط ها👇
            بین 5 تا 15 دقیقه 👈 1 
            بین 15 تا 30 دقیقه 👈 2 
            بین 30 تا 60 دقیقه 👈 3
            بیشتر از یک ساعت 👈 4
             خانه: ${_uiState.value.dailyReport.methodUsage.atHome ?: "-"}
             مدرسه (دانشگاه): ${_uiState.value.dailyReport.methodUsage.atSchool ?: "-"}
             غریبه ها: ${_uiState.value.dailyReport.methodUsage.withOthers ?: "-"}
             فامیل و آشنا: ${_uiState.value.dailyReport.methodUsage.withFamily ?: "-"}
            ☑️تعداد حساسیت زدایی: ${_uiState.value.dailyReport.desensitizationCount ?: "-"}
            ☑️تعداد لکنت عمدی: ${_uiState.value.dailyReport.intentionalStutteringCount ?: "-"}
            ☑️تعداد تشخیص اجتناب: ${_uiState.value.dailyReport.avoidanceDetectionCount ?: "-"}
            ☑️تعداد تماس همیاری: ${
            _uiState.value.dailyReport.callsCount.let {
                val res =
                    (it.teenSupportCallsCount ?: 0) + (it.adultSupportCallsCount ?: 0); if (res == 0) "-" else res
            }
        }
            ☑️تعداد تماس گروهی: ${_uiState.value.dailyReport.callsCount.groupCallsCount ?: "-"}
            ☑️تعداد چالش: ${_uiState.value.dailyReport.voicesProperties.challengesCount ?: "-"}
            ☑️چالش بر حسب دقیقه: ${_uiState.value.dailyReport.voicesProperties.sumOfChallengesDuration ?: "-"}
            ☑️کنفرانس بر حسب دقیقه: ${_uiState.value.dailyReport.voicesProperties.sumOfConferencesDuration ?: "-"}
            ☑️رضایت از خودم (1 تا 10): ${_uiState.value.dailyReport.selfSatisfaction ?: "-"}
            توضیحات: ${_uiState.value.dailyReport.description ?: "-"}
        """.trimIndent())
        val res = _uiState.value.dailyReports?.list?.add(_uiState.value.dailyReport)
        saveDailyReports()
        generateAdvice(_uiState.value.dailyReports?.list?.toList(), BaseApplication.ReportType.Daily)
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
        weeklyReport = _uiState.value.weeklyReport.copy(result = """
            ..#گزارش_هفتگی
            ${_uiState.value.weeklyReport.name ?: "ناشناس"}
            
            👈تعداد روز هایی که تمرینات انجام شده: ${_uiState.value.weeklyReport.practiceDays ?: "-"}
            👈تعداد روزهای کنفرانس دادن: ${_uiState.value.weeklyReport.voicesProperties.conferenceDaysCount ?: "-"}
            👈 مجموع کنفرانس هفته بر حسب دقیقه: ${_uiState.value.weeklyReport.voicesProperties.sumOfConferencesDuration ?: "-"}
            👈 مجموع چالش هفته بر حسب دقیقه: ${_uiState.value.weeklyReport.voicesProperties.sumOfChallengesDuration ?: "-"}
            👈تعداد شرکت در چالش (مثلا ۳ از n): ${_uiState.value.weeklyReport.voicesProperties.challengesCount ?: "-"}
            👈تعداد  تماس با همیار نوجوان: ${_uiState.value.weeklyReport.callsCount.teenSupportCallsCount ?: "-"}
            👈تعداد تماس با همیار بزرگسال: ${_uiState.value.weeklyReport.callsCount.adultSupportCallsCount ?: "-"}
            👈تعداد تماس گروهی: ${_uiState.value.weeklyReport.callsCount.groupCallsCount ?: "-"}
            👈تعداد گزارش حساسیت زدایی هفته: ${_uiState.value.weeklyReport.desensitizationCount ?: "-"}
            👈خلق استثنای هفته: ${_uiState.value.weeklyReport.creationOfExceptionCount ?: "-"}
            👈تعداد ارسال گزارش روزانه درهفته: ${_uiState.value.weeklyReport.dailyReportsCount ?: "-"}
            👈مجموع فعالیت ها: ${_uiState.value.weeklyReport.sumOfActivities ?: 0}
            
            ◾توضیحات اضافه: ${_uiState.value.weeklyReport.description ?: "-"}
        """.trimIndent())
        val res = _uiState.value.weeklyReports?.list?.add(_uiState.value.weeklyReport)
        saveWeeklyReports()
        generateAdvice(_uiState.value.weeklyReports?.list?.toList(), BaseApplication.ReportType.Weekly)
        return res == true
    }

    private fun saveWeeklyReports() {
        Log.i("TAG", "saving weekly reports json: ${toJson(_uiState.value.weeklyReports)}")
        context.dataStore[BaseApplication.Key.WeeklyReports] =
            toJson(_uiState.value.weeklyReports) ?: ""
    }
}