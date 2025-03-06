package ir.saltech.sokhanyar.ui.view.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.Constants.OTP_EXPIRATION_DURATION_SECONDS
import ir.saltech.sokhanyar.BaseApplication.Key.Chats
import ir.saltech.sokhanyar.R
import ir.saltech.sokhanyar.api.config.ApiCallback
import ir.saltech.sokhanyar.api.config.ApiClient
import ir.saltech.sokhanyar.api.config.call
import ir.saltech.sokhanyar.api.request.AccessTokenRequest
import ir.saltech.sokhanyar.api.request.AnalyzeReportRequest
import ir.saltech.sokhanyar.api.request.GenerateMotivationTextRequest
import ir.saltech.sokhanyar.api.request.OtpCodeRequest
import ir.saltech.sokhanyar.api.request.RegisterDeviceRequest
import ir.saltech.sokhanyar.api.response.AccessTokenResponse
import ir.saltech.sokhanyar.api.response.AnalyzeReportResponse
import ir.saltech.sokhanyar.api.response.ClinicsInfoResponse
import ir.saltech.sokhanyar.api.response.ErrorResponse
import ir.saltech.sokhanyar.api.response.GenerateMotivationTextResponse
import ir.saltech.sokhanyar.api.response.MessageResponse
import ir.saltech.sokhanyar.api.response.RegisterDeviceResponse
import ir.saltech.sokhanyar.model.api.DonationPayment
import ir.saltech.sokhanyar.model.api.PaymentResult
import ir.saltech.sokhanyar.model.data.general.Clinic
import ir.saltech.sokhanyar.model.data.general.Device
import ir.saltech.sokhanyar.model.data.general.OtpRequestStatus
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.general.UserRole
import ir.saltech.sokhanyar.model.data.messenger.Chats
import ir.saltech.sokhanyar.model.data.treatment.report.CallsCount
import ir.saltech.sokhanyar.model.data.treatment.report.DailyReport
import ir.saltech.sokhanyar.model.data.treatment.report.DailyReports
import ir.saltech.sokhanyar.model.data.treatment.report.Report
import ir.saltech.sokhanyar.model.data.treatment.report.VoicesProperties
import ir.saltech.sokhanyar.model.data.treatment.report.WeeklyReport
import ir.saltech.sokhanyar.model.data.treatment.report.WeeklyReports
import ir.saltech.sokhanyar.ui.state.MainUiState
import ir.saltech.sokhanyar.util.analyzeError
import ir.saltech.sokhanyar.util.dataStore
import ir.saltech.sokhanyar.util.fromJson
import ir.saltech.sokhanyar.util.get
import ir.saltech.sokhanyar.util.getGreetingBasedOnTime
import ir.saltech.sokhanyar.util.getLastDailyReports
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
import kotlin.random.Random

class MainViewModel : ViewModel() {
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
	private var realAdvice: MutableState<String?> = mutableStateOf(null)
		set(value) {
			_uiState.update { it.copy(realAdvice = value) }
		}
	private var chats: StateFlow<Chats> = MutableStateFlow(Chats())
		set(value) {
			_uiState.update { it.copy(chats = value) }
		}
	private var dailyReports: DailyReports? = null
		set(value) {
			_uiState.update { it.copy(dailyReports = value) }
		}
	private var weeklyReports: WeeklyReports? = null
		set(value) {
			_uiState.update { it.copy(weeklyReports = value) }
		}
	private var clinics: List<Clinic> = listOf()
		set(value) {
			_uiState.update { it.copy(clinics = value) }
		}

	var activePages: MutableList<BaseApplication.Page> =
		mutableStateListOf(BaseApplication.Page.Home)
		set(value) {
			_uiState.update { it.copy(activePages = value) }
		}

	var currentUser: User? = null
		set(value) {
			_uiState.update { it.copy(currentUser = value) }
		}
	var currentDailyReport: DailyReport? = null
		set(value) {
			_uiState.update { it.copy(currentDailyReport = value) }
		}
	var currentWeeklyReport: WeeklyReport? = null
		set(value) {
			_uiState.update { it.copy(currentWeeklyReport = value) }
		}


	// --- AI Section ---

	fun startOverChat() {
		viewModelScope.launch {
			// TODO: You can call another api keys and change model here
			chats = MutableStateFlow(Chats())
			saveChats()
		}
	}

	// TODO: ذخیره سازی و استفتده از چت با هوش مصنوعی رو براساس این اندپوینت های جدید بنویس
	/*
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
				if (_uiState.value.chats.value.contents.lastOrNull()?.content == "...") {
					viewModelScope.launch(Dispatchers.IO) {
						chats =
							MutableStateFlow(_uiState.value.chats.value.copy(contents = _uiState.value.chats.value.contents.let {
								it[it.size - 1] = ChatMessage(
									0,
									"assistant",
									"❌ ناتوانی در دریافت پیام از هوش مصنوعی!"
								); it
							}))
						delay(3000)
						chats =
							MutableStateFlow(_uiState.value.chats.value.copy(contents = _uiState.value.chats.value.contents.let { contents ->
								repeat(2) { contents.removeAt(contents.size - 1) }; contents
							}))
						delay(10)
						saveChatHistory()
						delay(10)
						if (_uiState.value.chats.value.contents.size in 0..1) {
							startOverChat()
						}
					}
				}
			}
		}
	}
	*/

	private fun generateAdviceForReport(
		reports: List<Report>?,
		reportType: BaseApplication.ReportType,
	) {
		if (reports != null && reports.size >= 2) {
			viewModelScope.launch {
				val analyzeReportsRequest = AnalyzeReportRequest(
					currentReport = reports.last(),
					lastReports = reports.subList(0, reports.lastIndex)
				)
				ApiClient.sokhanyar.analyzeTreatmentReport(
					accessToken = currentUser!!.device?.accessToken ?: return@launch,
					reportType = reportType.name,
					request = analyzeReportsRequest
				).call(object : ApiCallback<AnalyzeReportResponse> {
					override fun onSuccessful(responseObject: AnalyzeReportResponse?) {
						if (responseObject != null) {
							realAdvice.value = responseObject.advice.trim()
							_uiState.value.realAdvice.value = responseObject.advice.trim()
						}
					}

					override fun onFailure(
						response: ErrorResponse?,
						t: Throwable?,
					) {
						_errorMessage.value =
							response?.detail?.message ?: t?.message ?: "خطای نامشخص"
						t?.printStackTrace()
					}
				})
			}
		}
	}

	fun generateNewMotivationText() {
		viewModelScope.launch {
			ApiClient.sokhanyar.generateMotivationText(
				accessToken = currentUser!!.device?.accessToken ?: return@launch,
				request = GenerateMotivationTextRequest(prompt = " ${getGreetingBasedOnTime(true)}  یک جمله انگیزشی جذاب و متفاوت به من بگو.\n" + "فقط جمله انگیزشی ات رو نشون بده. جمله انگیزشی باید حداکثر 1 خط، به همراه ایموجی جذاب و بعضی مواقع در موضوع درمان لکنت باشه.\n")
			).call(
				object : ApiCallback<GenerateMotivationTextResponse> {
					override fun onSuccessful(responseObject: GenerateMotivationTextResponse?) {
						sentence = responseObject?.motivationText?.trim() ?: sentence
					}

					override fun onFailure(
						response: ErrorResponse?,
						t: Throwable?,
					) {
						_errorMessage.value =
							response?.detail?.message ?: t?.message ?: "خطای نامشخص"
						t?.printStackTrace()
					}
				}
			)
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
			if (_uiState.value.currentDailyReport!!.voicesProperties == VoicesProperties()) {
				currentDailyReport =
					_uiState.value.currentDailyReport!!.copy(
						voicesProperties = getVoicesProperties(
							context
						)
					)
			}
		}
	}

	fun getDefaultWeeklyReport(): WeeklyReport? {
		val res =
			if (_uiState.value.dailyReports != null && _uiState.value.dailyReports?.list?.isNotEmpty() == true) {
				val lastDailyReports: List<DailyReport> =
					_uiState.value.dailyReports?.getLastDailyReports() ?: return null
				_uiState.value.currentWeeklyReport!!.copy(
					patient = _uiState.value.currentUser!!,
					voicesProperties = lastDailyReports.let { lastReports ->
						VoicesProperties(
							lastReports.sumOf { lastDailyReport: DailyReport ->
								lastDailyReport.voicesProperties.challengesCount ?: 0
							}.takeIf { it > 0 },
							lastReports.sumOf { lastDailyReport: DailyReport ->
								lastDailyReport.voicesProperties.sumOfChallengesDuration ?: 0
							}.takeIf { it > 0 },
							lastReports.count { lastDailyReport: DailyReport ->
								lastDailyReport.voicesProperties.sumOfConferencesDuration != 0
							}.takeIf { it > 0 },
							lastReports.sumOf { lastDailyReport: DailyReport ->
								lastDailyReport.voicesProperties.sumOfConferencesDuration ?: 0
							}.takeIf { it > 0 })
					},
					practiceDays = lastDailyReports.count { (it.practiceTime ?: 0) >= 3 }
						.takeIf { it > 0 },
					callsCount = lastDailyReports.let { lastReports ->
						CallsCount(
							lastReports.sumOf { lastDailyReport: DailyReport ->
								lastDailyReport.callsCount.groupCallsCount ?: 0
							}.takeIf { it > 0 },
							lastReports.sumOf { lastDailyReport: DailyReport ->
								lastDailyReport.callsCount.peerCallsCount ?: 0
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

	private fun loadDailyReports() {
		dailyReports = fromJson<DailyReports>(
			context.dataStore[BaseApplication.Key.DailyReports] ?: ""
		) ?: DailyReports()
		currentDailyReport = _uiState.value.currentDailyReport?.copy(
			patient = _uiState.value.currentUser!!, date = Clock.System.now().toEpochMilliseconds()
		)
		Log.i("TAG", "Latest daily reports fetched: ${_uiState.value.dailyReports}")
	}

	fun saveDailyReport(): Boolean {
		currentUser = _uiState.value.currentDailyReport!!.patient
		currentDailyReport = _uiState.value.currentDailyReport!!.copy(
			result = """
            📝"فرم گزارش روزانه"
            ◾️تاریخ: ${Date(_uiState.value.currentDailyReport!!.date!!).toJalali().toReportDate()} 
            ◾️نام: ${(_uiState.value.currentDailyReport!!.patient.displayName ?: "").ifEmpty { "ناشناس" }}
            ☑️مدت زمان تمرین: ${_uiState.value.currentDailyReport!!.practiceTime?.toRegularTime() ?: "-"}
            ☑️مدت زمان اجرای شیوه در انواع محیط ها👇
            بین 5 تا 15 دقیقه 👈 1 
            بین 15 تا 30 دقیقه 👈 2 
            بین 30 تا 60 دقیقه 👈 3 
            بیشتر از یک ساعت 👈 4 
             خانه: ${_uiState.value.currentDailyReport!!.treatMethodUsage.atHome ?: "-"}
             مدرسه (دانشگاه): ${_uiState.value.currentDailyReport!!.treatMethodUsage.atSchool ?: "-"}
             غریبه ها: ${_uiState.value.currentDailyReport!!.treatMethodUsage.withOthers ?: "-"}
             فامیل و آشنا: ${_uiState.value.currentDailyReport!!.treatMethodUsage.withFamily ?: "-"}
            ☑️تعداد حساسیت زدایی: ${_uiState.value.currentDailyReport!!.desensitizationCount ?: "-"}
            ☑️تعداد لکنت عمدی: ${_uiState.value.currentDailyReport!!.intentionalStutteringCount ?: "-"}
            ☑️تعداد تشخیص اجتناب: ${_uiState.value.currentDailyReport!!.avoidanceDetectionCount ?: "-"}
            ☑️تعداد تماس همیاری: ${_uiState.value.currentDailyReport!!.callsCount.peerCallsCount ?: "-"}
            ☑️تعداد تماس گروهی: ${_uiState.value.currentDailyReport!!.callsCount.groupCallsCount ?: "-"}
            ☑️تعداد چالش: ${_uiState.value.currentDailyReport!!.voicesProperties.challengesCount ?: "-"}
            ☑️چالش بر حسب دقیقه: ${_uiState.value.currentDailyReport!!.voicesProperties.sumOfChallengesDuration ?: "-"}
            ☑️کنفرانس بر حسب دقیقه: ${_uiState.value.currentDailyReport!!.voicesProperties.sumOfConferencesDuration ?: "-"}
            ☑️رضایت از خودم (1 تا 10): ${_uiState.value.currentDailyReport!!.selfSatisfaction ?: "-"}
            توضیحات: ${_uiState.value.currentDailyReport!!.description ?: "-"}
        """.trimIndent()
		)
		val res = _uiState.value.dailyReports?.list?.add(_uiState.value.currentDailyReport!!)
		saveDailyReports()
		if ((_uiState.value.dailyReports?.list?.size ?: 0) > 1) {
			generateAdviceForReport(
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

	private fun loadWeeklyReports() {
		weeklyReports =
			fromJson<WeeklyReports>(context.dataStore[BaseApplication.Key.WeeklyReports] ?: "")
				?: WeeklyReports()
		currentWeeklyReport = _uiState.value.currentWeeklyReport?.copy(
			patient = _uiState.value.currentUser!!, date = Clock.System.now().toEpochMilliseconds()
		)
		Log.i("TAG", "Latest weekly reports fetched: ${_uiState.value.weeklyReports}")
	}

	fun saveWeeklyReport(): Boolean {
		currentUser = _uiState.value.currentWeeklyReport!!.patient
		currentWeeklyReport = _uiState.value.currentWeeklyReport!!.copy(
			result = """
            ..#گزارش_هفتگی
            👤 ${_uiState.value.currentWeeklyReport!!.patient.displayName ?: "ناشناس"}
            
            👈تعداد روز هایی که تمرینات انجام شده: ${_uiState.value.currentWeeklyReport!!.practiceDays ?: "-"}
            👈تعداد روزهای کنفرانس دادن: ${_uiState.value.currentWeeklyReport!!.voicesProperties.conferenceDaysCount ?: "-"}
            👈 مجموع کنفرانس هفته بر حسب دقیقه: ${_uiState.value.currentWeeklyReport!!.voicesProperties.sumOfConferencesDuration ?: "-"}
            👈 مجموع چالش هفته بر حسب دقیقه: ${_uiState.value.currentWeeklyReport!!.voicesProperties.sumOfChallengesDuration ?: "-"}
            👈تعداد شرکت در چالش (مثلا ۳ از n): ${_uiState.value.currentWeeklyReport!!.voicesProperties.challengesCount ?: "-"}
            👈تعداد  تماس همیاری: ${_uiState.value.currentWeeklyReport!!.callsCount.peerCallsCount ?: "-"}
            👈تعداد تماس گروهی: ${_uiState.value.currentWeeklyReport!!.callsCount.groupCallsCount ?: "-"}
            👈تعداد گزارش حساسیت زدایی هفته: ${_uiState.value.currentWeeklyReport!!.desensitizationCount ?: "-"}
            👈خلق استثنای هفته: ${_uiState.value.currentWeeklyReport!!.creationOfExceptionCount ?: "-"}
            👈تعداد ارسال گزارش روزانه درهفته: ${_uiState.value.currentWeeklyReport!!.dailyReportsCount ?: "-"}
            👈مجموع فعالیت ها: ${_uiState.value.currentWeeklyReport!!.sumOfActivities ?: 0}
            
            ◾توضیحات اضافه: ${_uiState.value.currentWeeklyReport!!.description ?: "-"}
        """.trimIndent()
		)
		val res = _uiState.value.weeklyReports?.list?.add(_uiState.value.currentWeeklyReport!!)
		saveWeeklyReports()
		if ((_uiState.value.weeklyReports?.list?.size ?: 0) > 1) {
			generateAdviceForReport(
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

	private fun saveChats() {
		Log.i("TAG", "saving chats json: ${toJson(_uiState.value.chats.value)}")
		context.dataStore[Chats] =
			toJson(_uiState.value.chats.value) ?: ""
	}

	private fun loadChats() {
		chats = MutableStateFlow(
			fromJson<Chats>(context.dataStore[Chats] ?: "")
				?: Chats()
		)
		_uiState.value.chats = MutableStateFlow(
			fromJson<Chats>(context.dataStore[Chats] ?: "")
				?: Chats()
		)
		Log.i(
			"TAG",
			"Latest chats list fetched: ${_uiState.value.chats.value}"
		)
	}

	fun saveUser() {
		Log.i("TAG", "saving user json: ${toJson(_uiState.value.currentUser)}")
		context.dataStore[BaseApplication.Key.User] = toJson(_uiState.value.currentUser) ?: ""
	}

	private fun loadUser() {
		currentUser = fromJson<User>(context.dataStore[BaseApplication.Key.User] ?: "") ?: User()
		if (_uiState.value.currentUser?.device == null) {
			Log.i("TAG", "User not registered in account!")
			activePages = mutableStateListOf(BaseApplication.Page.Login)
		} else {
			activePages = mutableStateListOf(BaseApplication.Page.Home)
		}
		Log.i("TAG", "User loaded -> ${_uiState.value.currentUser}")
	}

	fun loadPresets() {
		viewModelScope.launch {
			loadUser()
			generateNewMotivationText()
			loadDailyReports()
			loadWeeklyReports()
			loadChats()
		}
	}

	fun getClinicsList() {
		viewModelScope.launch {
			ApiClient.sokhanyar.getClinicsInfo().call(object : ApiCallback<ClinicsInfoResponse> {
				override fun onSuccessful(responseObject: ClinicsInfoResponse?) {
					if (responseObject != null) {
						clinics = responseObject.clinics
					}
				}

				override fun onFailure(
					response: ErrorResponse?,
					t: Throwable?,
				) {
					Log.e("GET_CLINICS", "Failed to get clinics items... ${response?.detail}")
					t?.printStackTrace()
				}
			})
		}
	}

	fun registerUserDevice(selectedClinic: Clinic, userRole: UserRole = UserRole.Patient) {
		viewModelScope.launch {
			ApiClient.sokhanyar.registerDevice(
				RegisterDeviceRequest(
					clinicId = selectedClinic.id,
					phoneNumber = _uiState.value.currentUser!!.phoneNumber ?: return@launch,
					userRole = userRole.name
				)
			).call(object : ApiCallback<RegisterDeviceResponse> {
				override fun onSuccessful(responseObject: RegisterDeviceResponse?) {
					if (responseObject != null) {
						currentUser = _uiState.value.currentUser?.copy(
							id = responseObject.userId,
							device = Device(id = responseObject.deviceId)
						)
					}
				}

				override fun onFailure(
					response: ErrorResponse?,
					t: Throwable?,
				) {
					TODO("Not yet implemented")
				}
			})
		}
	}

	fun requestOtpCode() {
		// TODO: باید فیلدی برای مشخص کردن اسم کلینیک توسط کاربر مثل یک dropdown فراهم بشه.
		ApiClient.sokhanyar.requestOtpCodeAuth(OtpCodeRequest(deviceId = _uiState.value.currentUser!!.device!!.id))
			.call(callback = object : ApiCallback<MessageResponse> {
				override fun onSuccessful(messageResponse: MessageResponse?) {
					Log.i("TAG", "Login result: $messageResponse")
					if (messageResponse != null) {
						_errorMessage.value = ""
						setOtpCodeStatus(OtpRequestStatus.REQUESTED)
					} else {
						setOtpCodeStatus(OtpRequestStatus.ERROR)
						_errorMessage.value = context.getString(R.string.unknown_error_occurred)
						Log.e("TAG", "failed to login ... get otp code")
					}
				}

				override fun onFailure(
					response: ErrorResponse?, t: Throwable?,
				) {
					setOtpCodeStatus(OtpRequestStatus.ERROR)
					_errorMessage.value =
						(response?.detail?.message ?: t?.message ?: "").analyzeError()
					t?.printStackTrace()
				}

			})
	}

	fun requestAccessToken(otpCode: Int, onCompleted: () -> Unit) {
		ApiClient.sokhanyar.requestAccessTokenAuth(
			AccessTokenRequest(
				deviceId = _uiState.value.currentUser!!.device!!.id,
				otpCode = otpCode
			)
		)
			.call(callback = object : ApiCallback<AccessTokenResponse> {
				override fun onSuccessful(responseObject: AccessTokenResponse?) {
					Log.i("TAG", "Verification result: $responseObject")
					if (responseObject != null) {
						_errorMessage.value = ""
						setAuthTokensToDevice(
							responseObject.refreshToken,
							responseObject.accessToken,
							responseObject.tokenType
						)
						saveUser()
						Log.i("TAG", "Login result: $responseObject")
						onCompleted()
					} else {
						_errorMessage.value = context.getString(R.string.unknown_error_occurred)
						Log.i("TAG", "Login result error with NULL")
					}
				}

				override fun onFailure(
					response: ErrorResponse?, t: Throwable?,
				) {
					_errorMessage.value =
						(response?.detail?.message ?: t?.message ?: "").analyzeError()
					t?.printStackTrace()
				}
			})
	}

	fun doStartPayment(price: Long, onCompleted: (Long?) -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			val phoneNumber = _uiState.value.currentUser!!.phoneNumber
			ApiClient.saltechPay.startDonationPayment(
				DonationPayment(
					price,
					"0${phoneNumber}",
					orderId = "SKN-${Random.nextInt(100000, 1000000)}"
				)
			)
				.call(callback = object : ApiCallback<PaymentResult> {
					override fun onSuccessful(responseObject: PaymentResult?) {
						Log.i("TAG", "Payment result: $responseObject")
						if (responseObject?.status == "success") {
							onCompleted(responseObject.trackId)
						} else {
							onCompleted(null)
							Toast.makeText(
								context,
								"خطایی در هنگام شروع عملیات پرداخت رخ داد!\n${responseObject?.message}",
								Toast.LENGTH_SHORT
							).show()
							Log.i("TAG", "Verification result error ${responseObject?.message}")
						}
					}

					override fun onFailure(
						response: ErrorResponse?, t: Throwable?,
					) {
						onCompleted(null)
						Toast.makeText(
							context,
							"خطایی در هنگام شروع عملیات پرداخت رخ داد!",
							Toast.LENGTH_SHORT
						).show()
						t?.printStackTrace()
					}
				})
			saveUser()
		}
	}

	fun setOtpCodeStatus(status: OtpRequestStatus) {
		currentUser =
			_uiState.value.currentUser!!.let {
				it.copy(device = it.device!!.copy(otpRequestStatus = status))
			}
	}

	private fun setAuthTokensToDevice(
		refreshToken: String,
		accessToken: String,
		tokenType: String = "bearer",
	) {
		currentUser =
			_uiState.value.currentUser!!.let {
				it.copy(
					device = it.device!!.copy(
						refreshToken = refreshToken,
						accessToken = accessToken,
						tokenType = tokenType
					)
				)
			}
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