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
import ir.saltech.ai.client.generativeai.GenerativeModel
import ir.saltech.ai.client.generativeai.type.content
import ir.saltech.ai.client.generativeai.type.generationConfig
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.dto.model.api.ChatHistory
import ir.saltech.myapps.stutter.dto.model.api.ChatMessage
import ir.saltech.myapps.stutter.dto.model.data.general.User
import ir.saltech.myapps.stutter.dto.model.data.reports.CallsCount
import ir.saltech.myapps.stutter.dto.model.data.reports.DailyReport
import ir.saltech.myapps.stutter.dto.model.data.reports.DailyReports
import ir.saltech.myapps.stutter.dto.model.data.reports.Report
import ir.saltech.myapps.stutter.dto.model.data.reports.VoicesProperties
import ir.saltech.myapps.stutter.dto.model.data.reports.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.data.reports.WeeklyReports
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.util.asAiContent
import ir.saltech.myapps.stutter.util.asAiContents
import ir.saltech.myapps.stutter.util.asChatMessage
import ir.saltech.myapps.stutter.util.dataStore
import ir.saltech.myapps.stutter.util.epochToFullDateTime
import ir.saltech.myapps.stutter.util.fromJson
import ir.saltech.myapps.stutter.util.get
import ir.saltech.myapps.stutter.util.getGreetingBasedOnTime
import ir.saltech.myapps.stutter.util.getLastDailyReports
import ir.saltech.myapps.stutter.util.getUserSummary
import ir.saltech.myapps.stutter.util.set
import ir.saltech.myapps.stutter.util.toJalali
import ir.saltech.myapps.stutter.util.toJson
import ir.saltech.myapps.stutter.util.toRegularTime
import ir.saltech.myapps.stutter.util.toReportDate
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

    //    private val openai = OpenAI(
//        token = BaseApplication.Constants.API_KEY,
//        host = OpenAIHost("${BaseApplication.Constants.BASE_URL}/v1/")
//    )
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

    var activePages: MutableList<BaseApplication.Page> =
        mutableStateListOf(BaseApplication.Page.Home)
        set(value) {
            _uiState.update { it.copy(activePages = value) }
        }
    var user: User = User()
        set(value) {
            _uiState.update { it.copy(user = value) }
        }
    var dailyReport: DailyReport = DailyReport()
        set(value) {
            _uiState.update { it.copy(dailyReport = value) }
        }
    var weeklyReport: WeeklyReport = WeeklyReport()
        set(value) {
            _uiState.update { it.copy(weeklyReport = value) }
        }
    private var chatHistory: StateFlow<ChatHistory> = MutableStateFlow(ChatHistory(0)).asStateFlow()
        set(value) {
            _uiState.update { it.copy(chatHistory = value) }
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

    fun startOverChat() {
        viewModelScope.launch {
            // TODO: You can call another api keys and change model here
            chatHistory = MutableStateFlow(ChatHistory(0))
            saveChatHistory()
        }
    }

    fun generateNewMessage(message: String) {
        viewModelScope.launch {
            val model = GenerativeModel(
                modelName = BaseApplication.Ai.Gemini.Models.Flash,
                BaseApplication.Ai.Gemini.apiKeys.random(),
                systemInstruction = content {
                    text(
                        "${BaseApplication.Ai.Gemini.BASE_SYSTEM_INSTRUCTIONS_V1_1}\nCurrent Time is ${
                            Clock.System.now().toEpochMilliseconds().epochToFullDateTime()
                        }"
                    )
                },
                generationConfig = generationConfig {
                    temperature = 1.3f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 1024
                    responseMimeType = "text/plain"
                    frequencyPenalty = 0.7f
                    presencePenalty = 1.2f
                }
            )
            val requestContent =
                ChatMessage(
                    (_uiState.value.chatHistory.value.contents.lastOrNull()?.id ?: -1) + 1,
                    "user",
                    message
                )
            chatHistory =
                MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let {
                    val userNameChatMessage = ChatMessage(
                        id = -1,
                        role = "user",
                        content = _uiState.value.user.getUserSummary()
                    )
                    if (_uiState.value.user.name != null && !it.contains(userNameChatMessage)) it.add(
                        0,
                        userNameChatMessage
                    )
                    it.add(requestContent)
                    it
                }))
            saveChatHistory()
            val backupChatHistory = _uiState.value.chatHistory.value.contents.asAiContents()
            chatHistory =
                MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let {
                    it.add(ChatMessage(0, "assistant", "...")); it
                }))
            val response =
                model.startChat(backupChatHistory)
                    .sendMessage(requestContent.asAiContent())
            Log.i("TAG", "Response got: $response")
            // TODO: You can set function calling here
            chatHistory =
                MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let {
                    it[_uiState.value.chatHistory.value.contents.lastIndex] =
                        (response.candidates[0].content.asChatMessage(_uiState.value.chatHistory.value.contents.lastOrNull())
                            ?: return@launch); it
                }))
            saveChatHistory()
        }
    }

    private fun generateAdvice(reports: List<Report>?, reportType: BaseApplication.ReportType) {
        if (reports != null && reports.size >= 2) {
            viewModelScope.launch {
                val model = GenerativeModel(
                    modelName = BaseApplication.Ai.Gemini.Models.Flash,
                    BaseApplication.Ai.Gemini.apiKeys.random(),
                    systemInstruction = content {
                        text(BaseApplication.Ai.Gemini.BASE_SYSTEM_INSTRUCTIONS_V1_1 + _uiState.value.user.getUserSummary())
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
                    BaseApplication.Ai.Gemini.apiKeys.random(),
                    generationConfig = generationConfig {
                        temperature = 1.5f
                        topK = 40
                        topP = 0.95f
                        maxOutputTokens = 100
                        responseMimeType = "text/plain"
                        frequencyPenalty = 1.5f
                    }
                )
                val generatedContent = model.generateContent(
                    " ${getGreetingBasedOnTime(true)}  یک جمله انگیزشی به من بگو.\n" +
                            "فقط جمله انگیزشی ات رو نشون بده. جلمه انگیزشی باید فقط 1 خط باشه و به همراه ایموجی جذاب باشه.\n"
                )
                sentence = generatedContent.text?.trim() ?: sentence
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // --- Process Reports Section ---

    private fun getVoicesProperties(context: Context): VoicesProperties {
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

    fun loadVoicesProperties() {
        viewModelScope.launch {
            if (_uiState.value.dailyReport.voicesProperties == VoicesProperties()) {
                dailyReport =
                    _uiState.value.dailyReport.copy(voicesProperties = getVoicesProperties(context))
            }
        }
    }

    fun getDefaultWeeklyReport(): WeeklyReport? {
        val res =
            if (_uiState.value.dailyReports != null && _uiState.value.dailyReports?.list?.isNotEmpty() == true) {
                val lastDailyReports: List<DailyReport> =
                    _uiState.value.dailyReports?.getLastDailyReports() ?: return null
                _uiState.value.weeklyReport.copy(user = _uiState.value.dailyReport.user,
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
//            name = _uiState.value.dailyReports?.list?.lastOrNull()?.name,
            user = _uiState.value.user,
            date = Clock.System.now().toEpochMilliseconds()
        )
        Log.i("TAG", "Latest daily reports fetched: ${_uiState.value.dailyReports}")
    }

    fun saveDailyReport(): Boolean {
        user = _uiState.value.dailyReport.user
        dailyReport = _uiState.value.dailyReport.copy(result = """
            📝"فرم گزارش روزانه"
            ◾️تاریخ: ${Date(_uiState.value.dailyReport.date!!).toJalali().toReportDate()} 
            ◾️نام: ${(_uiState.value.dailyReport.user.name ?: "").ifEmpty { "ناشناس" }}
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
        saveUser()
        generateAdvice(
            _uiState.value.dailyReports?.list?.toList(),
            BaseApplication.ReportType.Daily
        )
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
//            user = _uiState.value.weeklyReports?.list?.lastOrNull()?.user,
            user = _uiState.value.user,
            date = Clock.System.now().toEpochMilliseconds()
        )
        Log.i("TAG", "Latest weekly reports fetched: ${_uiState.value.weeklyReports}")
    }

    fun saveWeeklyReport(): Boolean {
        user = _uiState.value.weeklyReport.user
        weeklyReport = _uiState.value.weeklyReport.copy(
            result = """
            ..#گزارش_هفتگی
            ◾️ ${Date(_uiState.value.dailyReport.date!!).toJalali().toReportDate()} 
            👤 ${_uiState.value.weeklyReport.user.name ?: "ناشناس"}
            
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
        """.trimIndent()
        )
        val res = _uiState.value.weeklyReports?.list?.add(_uiState.value.weeklyReport)
        saveWeeklyReports()
        saveUser()
        generateAdvice(
            _uiState.value.weeklyReports?.list?.toList(),
            BaseApplication.ReportType.Weekly
        )
        return res == true
    }

    private fun saveWeeklyReports() {
        Log.i("TAG", "saving weekly reports json: ${toJson(_uiState.value.weeklyReports)}")
        context.dataStore[BaseApplication.Key.WeeklyReports] =
            toJson(_uiState.value.weeklyReports) ?: ""
    }

    private fun saveChatHistory() {
        Log.i("TAG", "saving chat history json: ${toJson(_uiState.value.chatHistory.value)}")
        context.dataStore[BaseApplication.Key.ChatHistory] =
            toJson(_uiState.value.chatHistory.value) ?: ""
    }

    fun loadChatHistory() {
        chatHistory = MutableStateFlow(
            fromJson<ChatHistory>(context.dataStore[BaseApplication.Key.ChatHistory] ?: "")
                ?: ChatHistory(0)
        )
        _uiState.value.chatHistory = MutableStateFlow(
            fromJson<ChatHistory>(context.dataStore[BaseApplication.Key.ChatHistory] ?: "")
                ?: ChatHistory(0)
        )
        Log.i(
            "TAG",
            "Latest chat history fetched: ${_uiState.value.chatHistory.value} || chat history 1 "
        )
    }

    fun saveUser() {
        Log.i("TAG", "saving user json: ${toJson(_uiState.value.user)}")
        context.dataStore[BaseApplication.Key.User] =
            toJson(_uiState.value.user) ?: ""
    }

    fun loadUser() {
        user = fromJson<User>(context.dataStore[BaseApplication.Key.User] ?: "")
            ?: User()
        if (user.name == null || user.age == null) {
            Log.i("TAG", "User not registered!")
            activePages = mutableStateListOf(BaseApplication.Page.Welcome)
        }
        Log.i("TAG", "User loaded -> ${_uiState.value.user}")
    }
}