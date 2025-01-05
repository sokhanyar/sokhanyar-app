package ir.saltech.sokhanyar.ui.view.model

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
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.Constants.OTP_EXPIRATION_DURATION_SECONDS
import ir.saltech.sokhanyar.R
import ir.saltech.sokhanyar.api.ApiCallback
import ir.saltech.sokhanyar.api.ApiClient
import ir.saltech.sokhanyar.api.call
import ir.saltech.sokhanyar.model.api.ChatHistory
import ir.saltech.sokhanyar.model.api.ChatMessage
import ir.saltech.sokhanyar.model.api.ErrorResponse
import ir.saltech.sokhanyar.model.api.ResponseObject
import ir.saltech.sokhanyar.model.data.general.AuthInfo
import ir.saltech.sokhanyar.model.data.general.OtpRequestStatus
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.reports.CallsCount
import ir.saltech.sokhanyar.model.data.reports.VoicesProperties
import ir.saltech.sokhanyar.model.data.reports.WeeklyReport
import ir.saltech.sokhanyar.model.data.reports.WeeklyReports
import ir.saltech.sokhanyar.ui.state.MainUiState
import ir.saltech.sokhanyar.util.analyzeError
import ir.saltech.sokhanyar.util.asAiContent
import ir.saltech.sokhanyar.util.asAiContents
import ir.saltech.sokhanyar.util.asChatMessage
import ir.saltech.sokhanyar.util.dataStore
import ir.saltech.sokhanyar.util.epochToFullDateTime
import ir.saltech.sokhanyar.util.fromJson
import ir.saltech.sokhanyar.util.get
import ir.saltech.sokhanyar.util.getGreetingBasedOnTime
import ir.saltech.sokhanyar.util.getLastDailyReports
import ir.saltech.sokhanyar.util.getUserSummary
import ir.saltech.sokhanyar.util.set
import ir.saltech.sokhanyar.util.toJalali
import ir.saltech.sokhanyar.util.toJson
import ir.saltech.sokhanyar.util.toRegularTime
import ir.saltech.sokhanyar.util.toReportDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
	//    private val openai = OpenAI(
//        token = BaseApplication.Constants.API_KEY,
//        host = OpenAIHost("${BaseApplication.Constants.BASE_URL}/v1/")
//    )
	private val _uiState = MutableStateFlow(MainUiState())
	private val _errorMessage = MutableStateFlow("")
	private val _remainingTime = MutableStateFlow(0L)
	val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
	val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()
	val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

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
	var dailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport =
		ir.saltech.sokhanyar.model.data.reports.DailyReport()
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
	private var dailyReports: ir.saltech.sokhanyar.model.data.reports.DailyReports =
		ir.saltech.sokhanyar.model.data.reports.DailyReports()
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
			try {
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
					})
				val requestContent = ChatMessage(
					(_uiState.value.chatHistory.value.contents.lastOrNull()?.id ?: -1) + 1,
					"user",
					message
				)
				chatHistory =
					MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let {
						val userNameChatMessage = ChatMessage(
							id = -1, role = "user", content = _uiState.value.user.getUserSummary()
						)
						if (_uiState.value.user.name != null && !it.contains(userNameChatMessage)) it.add(
							0, userNameChatMessage
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
					model.startChat(backupChatHistory).sendMessage(requestContent.asAiContent())
				Log.i("TAG", "Response got: $response")
				// TODO: You can set function calling here
				chatHistory =
					MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let {
						it[_uiState.value.chatHistory.value.contents.lastIndex] =
							(response.candidates[0].content.asChatMessage(_uiState.value.chatHistory.value.contents.lastOrNull())
								?: return@launch); it
					}))
				saveChatHistory()
			} catch (e: Exception) {
				e.printStackTrace()
				if (_uiState.value.chatHistory.value.contents.lastOrNull()?.content == "...") {
					viewModelScope.launch(Dispatchers.IO) {
						chatHistory =
							MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let {
								it[it.size - 1] = ChatMessage(0, "assistant", "❌ ناتوانی در دریافت پیام از هوش مصنوعی!"); it
							}))
						delay(3000)
						chatHistory =
							MutableStateFlow(_uiState.value.chatHistory.value.copy(contents = _uiState.value.chatHistory.value.contents.let { contents ->
								repeat(2) { contents.removeAt(contents.size - 1) }; contents
							}))
						delay(10)
						saveChatHistory()
						delay(10)
						if (_uiState.value.chatHistory.value.contents.size in 0..1) {
							startOverChat()
						}
					}
				}
			}
		}
	}

	private fun generateAdvice(
		reports: List<ir.saltech.sokhanyar.model.data.reports.Report>?,
		reportType: BaseApplication.ReportType
	) {
		if (reports != null && reports.size >= 2) {
			viewModelScope.launch {
				try {
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
						})
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
					advice.value = generatedResponse.text?.trim()
					_uiState.value.advice.value = generatedResponse.text?.trim()
				} catch (e: Exception) {
					e.printStackTrace()
					// TODO: بعداً یادت باشه این رو حذف کنی! چون باید advice رو ذخیره کنی و نباید خطا ها ذخیره بشن .. اینو طور دیگه هندل کن
					advice.value = "ناتوانی در دریافت توصیه از هوش مصنوعی!"
					_uiState.value.advice.value = "ناتوانی در دریافت توصیه از هوش مصنوعی!"
				}
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
					})
				val generatedContent = model.generateContent(
					" ${getGreetingBasedOnTime(true)}  یک جمله انگیزشی به من بگو.\n" + "فقط جمله انگیزشی ات رو نشون بده. جلمه انگیزشی باید فقط 1 خط باشه و به همراه ایموجی جذاب باشه.\n"
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
		val challenges = recordings.filter { it.first.startsWith("چالش") }
		val challengesCount = challenges.count()
		val sumOfChallengesDuration = challenges.sumOf { it.second }
		val conferences =
			recordings.filter { it.first.startsWith("کنفرانس") || it.first.startsWith("گزارش") }
		val sumOfConferencesDuration = conferences.sumOf { it.second }
		Log.i(
			"TAG",
			"result: recordings: $recordings , challenges: $challengesCount, sumOfChallengesDuration: $sumOfChallengesDuration , conferences: ${conferences.count()} , sumOfConferencesDuration: $sumOfConferencesDuration"
		)
		return VoicesProperties(
			challengesCount = challengesCount.takeIf { it > 0 },
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
				val lastDailyReports: List<ir.saltech.sokhanyar.model.data.reports.DailyReport> =
					_uiState.value.dailyReports?.getLastDailyReports() ?: return null
				_uiState.value.weeklyReport.copy(
					user = _uiState.value.dailyReport.user,
					voicesProperties = lastDailyReports.let { lastReports ->
						VoicesProperties(
							lastReports.sumOf { lastDailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport ->
								lastDailyReport.voicesProperties.challengesCount ?: 0
							}.takeIf { it > 0 },
							lastReports.sumOf { lastDailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport ->
								lastDailyReport.voicesProperties.sumOfChallengesDuration ?: 0
							}.takeIf { it > 0 },
							lastReports.count { lastDailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport ->
								lastDailyReport.voicesProperties.sumOfConferencesDuration != 0
							}.takeIf { it > 0 },
							lastReports.sumOf { lastDailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport ->
								lastDailyReport.voicesProperties.sumOfConferencesDuration ?: 0
							}.takeIf { it > 0 })
					},
					practiceDays = lastDailyReports.count { (it.practiceTime ?: 0) >= 3 }
						.takeIf { it > 0 },
					callsCount = lastDailyReports.let { lastReports ->
						CallsCount(
							lastReports.sumOf { lastDailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport ->
								lastDailyReport.callsCount.groupCallsCount ?: 0
							}.takeIf { it > 0 },
							lastReports.sumOf { lastDailyReport: ir.saltech.sokhanyar.model.data.reports.DailyReport ->
								lastDailyReport.callsCount.supportingP2PCallsCount ?: 0
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
		dailyReports = fromJson<ir.saltech.sokhanyar.model.data.reports.DailyReports>(
			context.dataStore[BaseApplication.Key.DailyReports] ?: ""
		) ?: ir.saltech.sokhanyar.model.data.reports.DailyReports()
		dailyReport = _uiState.value.dailyReport.copy(
//            name = _uiState.value.dailyReports?.list?.lastOrNull()?.name,
			user = _uiState.value.user, date = Clock.System.now().toEpochMilliseconds()
		)
		Log.i("TAG", "Latest daily reports fetched: ${_uiState.value.dailyReports}")
	}

	fun saveDailyReport(): Boolean {
		user = _uiState.value.dailyReport.user
		dailyReport = _uiState.value.dailyReport.copy(
			result = """
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
            ☑️تعداد تماس همیاری: ${_uiState.value.dailyReport.callsCount.supportingP2PCallsCount ?: "-"}
            ☑️تعداد تماس گروهی: ${_uiState.value.dailyReport.callsCount.groupCallsCount ?: "-"}
            ☑️تعداد چالش: ${_uiState.value.dailyReport.voicesProperties.challengesCount ?: "-"}
            ☑️چالش بر حسب دقیقه: ${_uiState.value.dailyReport.voicesProperties.sumOfChallengesDuration ?: "-"}
            ☑️کنفرانس بر حسب دقیقه: ${_uiState.value.dailyReport.voicesProperties.sumOfConferencesDuration ?: "-"}
            ☑️رضایت از خودم (1 تا 10): ${_uiState.value.dailyReport.selfSatisfaction ?: "-"}
            توضیحات: ${_uiState.value.dailyReport.description ?: "-"}
        """.trimIndent())
		val res = _uiState.value.dailyReports?.list?.add(_uiState.value.dailyReport)
		saveDailyReports()
		if ((_uiState.value.dailyReports?.list?.size ?: 0) > 1) {
			generateAdvice(
				_uiState.value.dailyReports?.list?.toList(), BaseApplication.ReportType.Daily
			)
		}
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
			user = _uiState.value.user, date = Clock.System.now().toEpochMilliseconds()
		)
		Log.i("TAG", "Latest weekly reports fetched: ${_uiState.value.weeklyReports}")
	}

	fun saveWeeklyReport(): Boolean {
		user = _uiState.value.weeklyReport.user
		weeklyReport = _uiState.value.weeklyReport.copy(
			result = """
            ..#گزارش_هفتگی
            👤 ${_uiState.value.weeklyReport.user.name ?: "ناشناس"}
            
            👈تعداد روز هایی که تمرینات انجام شده: ${_uiState.value.weeklyReport.practiceDays ?: "-"}
            👈تعداد روزهای کنفرانس دادن: ${_uiState.value.weeklyReport.voicesProperties.conferenceDaysCount ?: "-"}
            👈 مجموع کنفرانس هفته بر حسب دقیقه: ${_uiState.value.weeklyReport.voicesProperties.sumOfConferencesDuration ?: "-"}
            👈 مجموع چالش هفته بر حسب دقیقه: ${_uiState.value.weeklyReport.voicesProperties.sumOfChallengesDuration ?: "-"}
            👈تعداد شرکت در چالش (مثلا ۳ از n): ${_uiState.value.weeklyReport.voicesProperties.challengesCount ?: "-"}
            👈تعداد  تماس همیاری: ${_uiState.value.weeklyReport.callsCount.supportingP2PCallsCount ?: "-"}
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
		if ((_uiState.value.weeklyReports?.list?.size ?: 0) > 1) {
			generateAdvice(
				_uiState.value.weeklyReports?.list?.toList(), BaseApplication.ReportType.Weekly
			)
		}
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
		context.dataStore[BaseApplication.Key.User] = toJson(_uiState.value.user) ?: ""
	}

	fun loadUser() {
		user = fromJson<User>(context.dataStore[BaseApplication.Key.User] ?: "") ?: User()
//		if (_uiState.value.user.authInfo == null) {
//			Log.i("TAG", "User not registered in account!")
//            activePages = mutableStateListOf(BaseApplication.Page.Login)
//		} else
		if (_uiState.value.user.name == null || _uiState.value.user.age == null) {
			Log.i("TAG", "User not registered in form!")
			activePages = mutableStateListOf(BaseApplication.Page.Welcome)
		} else {
			activePages = mutableStateListOf(BaseApplication.Page.Home)
		}
		Log.i("TAG", "User loaded -> ${_uiState.value.user}")
	}

	fun loadPresets() {
		viewModelScope.launch {
			loadUser()
			generateNewMotivationText()
			loadDailyReports()
			loadWeeklyReports()
			loadChatHistory()
		}
	}

	fun doLogin(phoneNumber: Long) {
		ApiClient.sokhanyar.doSignIn(AuthInfo(phoneNumber = phoneNumber))
			.call(callback = object : ApiCallback<ResponseObject> {
				override fun onSuccessful(responseObject: ResponseObject?) {
					Log.i("TAG", "Login result: $responseObject")
					if (responseObject != null) {
						_errorMessage.value = ""
						user = _uiState.value.user.copy(
							authInfo = AuthInfo(phoneNumber = phoneNumber, otpRequestStatus = OtpRequestStatus.REQUESTED)
						)
					} else {
						_errorMessage.value = context.getString(R.string.unknown_error_occurred)
						user =
							_uiState.value.user.copy(authInfo = AuthInfo(otpRequestStatus = OtpRequestStatus.ERROR))
						Log.e("TAG", "failed to login ... get otp code")
					}
				}

				override fun onFailure(
					response: ErrorResponse?, t: Throwable?
				) {
					user =
						_uiState.value.user.copy(authInfo = AuthInfo(otpRequestStatus = OtpRequestStatus.ERROR))
					_errorMessage.value = (response?.detail?.message ?: t?.message ?: "").analyzeError()
					t?.printStackTrace()
				}

			})
	}

	fun doVerifyOtp(phoneNumber: Long, otpCode: Int, onCompleted: () -> Unit) {
		ApiClient.sokhanyar.verifyOtp(AuthInfo(phoneNumber = phoneNumber, otpCode = otpCode))
			.call(callback = object : ApiCallback<AuthInfo> {
				override fun onSuccessful(responseObject: AuthInfo?) {
					Log.i("TAG", "Verification result: $responseObject")
					if (responseObject != null) {
						_errorMessage.value = ""
						user = _uiState.value.user.copy(authInfo = responseObject.copy(phoneNumber = phoneNumber))
						saveUser()
						Log.i("TAG", "Verification result: $responseObject")
						onCompleted()
					} else {
						_errorMessage.value = context.getString(R.string.unknown_error_occurred)
						Log.i("TAG", "Verification result error with NULL")
					}
				}

				override fun onFailure(
					response: ErrorResponse?, t: Throwable?
				) {
					_errorMessage.value = (response?.detail?.message ?: t?.message ?: "").analyzeError()
					t?.printStackTrace()
				}
			})
	}

	fun resetOtpRequestStatus() {
		user =
			_uiState.value.user.copy(authInfo = AuthInfo(otpRequestStatus = OtpRequestStatus.NOT_REQUESTED))
	}

	fun startCountdown(durationSeconds: Long = OTP_EXPIRATION_DURATION_SECONDS) {
		viewModelScope.launch {
			for (i in durationSeconds downTo 0) {
				_remainingTime.value = i
				delay(1000)
			}
		}
	}

	fun resetErrorMessage() {
		_errorMessage.value = ""
	}
}