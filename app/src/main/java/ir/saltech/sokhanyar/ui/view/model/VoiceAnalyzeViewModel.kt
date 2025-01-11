package ir.saltech.sokhanyar.ui.view.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.saltech.ai.client.generativeai.GenerativeModel
import ir.saltech.ai.client.generativeai.type.FunctionType
import ir.saltech.ai.client.generativeai.type.ResponseStoppedException
import ir.saltech.ai.client.generativeai.type.Schema
import ir.saltech.ai.client.generativeai.type.asTextOrNull
import ir.saltech.ai.client.generativeai.type.content
import ir.saltech.ai.client.generativeai.type.generationConfig
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.api.ApiCallback
import ir.saltech.sokhanyar.api.ApiClient
import ir.saltech.sokhanyar.api.call
import ir.saltech.sokhanyar.model.api.ErrorResponse
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.general.Voice
import ir.saltech.sokhanyar.ui.state.VoiceAnalyzeUiState
import ir.saltech.sokhanyar.util.fromJson
import ir.saltech.sokhanyar.util.getMimeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File


class VoiceAnalyzeViewModel : ViewModel() {
	private val _uiState = MutableStateFlow(VoiceAnalyzeUiState())
	val uiState = _uiState.asStateFlow()

	@SuppressLint("StaticFieldLeak")
	lateinit var context: Context
	var user: User = User()
		set(value) {
			_uiState.update { _uiState.value.copy(user = value) }
		}
	var voice: Voice = Voice()
		set(value) {
			_uiState.update { _uiState.value.copy(voice = value) }
		}
	var mediaPlayer: MediaPlayer? = null
		set(value) {
			_uiState.update { _uiState.value.copy(mediaPlayer = value) }
		}
	private var wantedApiKey = BaseApplication.Ai.Gemini.apiKeys.random()


	private fun refreshApiKey() {
		wantedApiKey = BaseApplication.Ai.Gemini.apiKeys.random()
	}

	private fun resetVoiceAnalysis() {
		voice = _uiState.value.voice.copy(serverFile = null, error = null, response = null)
	}

	fun resetOperation(fromZero: Boolean = true) {
		if (fromZero) {
			resetVoiceAnalysis()
			refreshApiKey()
			startVoiceAnalyzing()
		} else {
			voice = _uiState.value.voice.copy(error = null)
			viewModelScope.launch {
				generateFeedback()
			}
		}
	}

	fun startVoiceAnalyzing() {
		if (_uiState.value.voice.selectedFile != null) {
			viewModelScope.launch {
				uploadVoiceToCloud()
				Log.i("TAG", "mime type is ${_uiState.value.voice.selectedFile?.getMimeType()}")
			}
		}
	}

	suspend fun generateFeedback() {
		try {
			val payload = listOf(
				content {
					text("practical voice: ")
				},
				content {
					(_uiState.value.voice.serverFile ?: return@content).let {
						fileData(it.uri, it.mimeType)
					}
				}
			)
			val loadableList = if (_uiState.value.voice.response?.feedbackOfFeedback != null) {
				listOf(
					content {
						text("transcribed practical voice: ")
					}, content {
						text(_uiState.value.voice.response?.transcription ?: return@content)
					},
					content {
						Log.i("TAG", "content is ? ${_uiState.value.voice.response?.lastFeedback}")
						text("feedback: ${getEnhancedResponse(true)} ${_uiState.value.voice.response?.lastFeedback ?: return@content}")
					}
				) + payload
			} else {
				payload
			}
			Log.i("TAG", "Let's generate response ...")
			val model = GenerativeModel(
				modelName = BaseApplication.Ai.Gemini.Models.Flash,
				apiKey = wantedApiKey,
				systemInstruction = content {
					text(BaseApplication.Ai.Gemini.BASE_SYSTEM_INSTRUCTIONS_V1_1 + "\nIf the voice is not understandable or translatable, reject it.")
				},
				generationConfig = generationConfig {
					temperature = 1.12f
					topK = 40
					topP = 0.95f
					maxOutputTokens = 8192
					responseMimeType = "application/json"
					responseSchema = Schema(
						name = "",
						description = "",
						properties = mapOf(
							"response" to Schema(
								"",
								"",
								properties = mapOf(
									"feedback" to Schema.str("", ""),
									"transcription" to Schema.str(
										"",
										""
									)
								),
								required = listOf("feedback"),
								type = FunctionType.OBJECT
							)
						),
						required = listOf("response"),
						type = FunctionType.OBJECT
					)
				}
			)
			val chatHist = listOf(
				content {
					text("transcribed practical voice: ")
				},
				content {
					text("خب. <noise> به این پی بردم <noise> که <noise> خونمون <noise> نه <noise> آنتن <noise> میده <noise> نه خط <noise> تلفن <noise> داریم <noise> و نه <noise> هیچ‌چی. <noise> و وقتی <noise> شما <noise> آنتن <noise> نداشته <noise> باشید <noise> مسلماً <noise> اینترنت با <noise> بالونی <noise> میاد. <noise> وقتی <noise> اینترنت با <noise> بالونی <noise> میاد <noise> وای‌فای <noise> کنفرانس‌های <noise> تو هم <noise> نمی‌آد. <noise> و <noise> تازه کشف کردم <noise> که موشک <noise> اینترنت <noise> و <noise> از تمام <noise> دار و <noise> ندار <noise> سازمانی <noise> ایرانی <noise> عوض <noise> خواهی <noise> می‌کنه <noise> اینقدر <noise> فوششون <noise> دادم <noise> سازمانی <noise> وای‌فای <noise> و اینکه <noise> پنج دقیقه <noise> تمرین <noise> من <noise> تموم <noise> شد <noise> و اینکه <noise> برم <noise> یه جایی <noise> که <noise> آنتن <noise> داشته <noise> باشیم <noise> و وای‌فای‌اش <noise> یا عالی <noise> خود آنتن یا <noise>")
				},
				content {
					text("feedback: سلام پسر عزیزم \nصبح جمعه ی شما هم به شادی ❤️\nخوب به این پی بردی که \nخونه تون آنتن نمیده 😔\nو برنامه ی ایتا برات بالا نمیاد \nو حالا متوجه شدی بخاطر برنامه ی ایتا نیست 😂\nو الان عذر خواهی کردی از برنامه نویسان🙈😁\n\nرعایت شیوه ✅")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text(
						"سلام. <noise> صبح <noise> آخر <noise> هفته‌تون <noise> بخیر. <noise> باشه. <noise> امروز <noise> که <noise> کلاس <noise> خاصی <noise> نداشتم <noise> و <noise> برای <noise> توی <noise> خونه <noise> هستم <noise> و <noise> کاری <noise> انجام <noise> افتادم رو <noise> انجام <noise> می‌دم <noise> چون که <noise> شنبه <noise> و یکشنبه <noise> امتحان <noise> دارم <noise> برای <noise> امروز <noise> شروع به <noise> خوندن <noise> کردم <noise> و تا ظهر کارمو <noise> انجام <noise> بدم <noise> و <noise> ظهر <noise> کارمو <noise> انجام <noise> بدم و <noise> برم <noise> خونه <noise> نیما <noise> و <noise> تا شب <noise> اونجا هستیم و <noise> درس <noise> می‌خونم <noise> ولی <noise> چون مامان و <noise> بابام <noise> شیفت <noise> هستن <noise> و داداش <noise> هم که <noise> صبح <noise> می‌خواد <noise> بره <noise> خونه <noise> یه <noise> مامان <noise> بزرگ <noise> و من <noise> توی <noise> خونه <noise> تنها هستم <noise> چون <noise> اونجا <noise> خیلی <noise> سر و <noise> صدا از <noise> بازی <noise> اونا <noise> بلند <noise> می‌شه <noise> و <noise> می‌رم <noise> خونه"
					)
				},
				content {
					text("feedback: سلام صبح بخیر😍\nامروز تو خونه میمونی چون\nکارهای عقب افتاده داری \nشنبه و یکشنبه امتحان داری 😬\nباید شروع به خوندن کنی 😁\nظهر هم میری خونه ی مامانبزرگ 👌 به به بهت خوش بگزره \nرعایت شیوه ✅")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text(
						"سلام به <noise> دوستان. <noise> صبح‌تون بخیر. <noise> می‌خوام براتون <noise> اسمو ببخشید. <noise> یه سخن <noise> یه خورده <noise> چیز <noise> خب <noise> علی مشایی هستم <noise> و موضوع <noise> موضوع کنفرانس <noise> در مورد تاریخچه <noise> شرکت <noise> لامبورگینی <noise> و <noise> بعدش <noise> دیگه به <noise> خواب <noise> به خواب یه <noise> شیوه <noise> شیوه کشیده گویی <noise> و دیگه <noise> می‌خوام <noise> خب شروع <noise> می‌کنیم. <noise> خب. <noise> لامبورگینی که خب ما همه‌مون <noise> می‌شناسیمش <noise> و می‌دونیم <noise> که خب ماشین‌های <noise> سوپر اسپورت <noise> خفنی <noise> می‌سازه. <noise> لامبورگینی که فامیلی تاسیس <noise> کرده این شرکت <noise> که اسم کاملش <noise> فروچیو فروچیو لامبورگینی <noise> این این مرد <noise> یعنی <noise> کارش کشاورزی <noise> بود <noise> و خب می‌دونیم <noise> که <noise> لامبورگینی برای <noise> کشور <noise> ایتالیاست. <noise> و خب <noise> توی دهه ۶۰ <noise> میلادی <noise> یعنی <noise> ۱۹۶۰ <noise> خب ایتالیا از لحاظ <noise> ببخشید. <noise> از لحاظ <noise> کشاورزی <noise> خیلی <noise> ضعیف و <noise> فقیر <noise> بود <noise> و خب <noise> فروچیو <noise> می‌خواست یه کار <noise> بزرگی <noise> انجام بده. <noise> یعنی تراکتور <noise> بسازه. <noise> و خب <noise> حتی <noise> به پدرش <noise> هم که گفت <noise> پدرش اونو قبول نداشت. <noise> بعد خب توی اون زمان <noise> جنگ جهانی <noise> دوم <noise> شروع می‌شه <noise> و چون فروچیو <noise> جوون <noise> بوده <noise> جوون بوده <noise> برای جنگ <noise> انتخاب می‌شه <noise> بعدش دیگه <noise> دیگه <noise> بعد یعنی وقتی می‌ره <noise> توی جنگ <noise> کارش اونجا مکانیکی <noise> و بعد خب <noise> تا اینجایی <noise> بود قضیه. <noise> بعد چندین ماه و یا چند سال <noise> که جنگ تموم می‌شه <noise> فروچیو <noise> میاد با قطعات <noise> ماشین‌های <noise> نظامی <noise> یه تراکتور می‌سازه <noise> و موفق می‌شه. <noise> می‌ره همه <noise> موتورها رو برمی‌داره <noise> و خب شرکت <noise> خودش رو می‌سازه. <noise> بعد یه سال دو سال <noise> می‌بینه دیگه <noise> قطعاتش <noise> داره تموم می‌شه. <noise> و خب شروع می‌کنه <noise> به تولید قطعات. <noise> و خب موفق هم <noise> می‌شه <noise> بعد <noise> وقتی دیگه به اون <noise> درآمد <noise> رسید <noise> فراری <noise> می‌خره. <noise> این فراری‌ها <noise> خب مشکل <noise> کلاچ داشتن <noise> این فروچیو <noise> می‌ره پیش <noise> پیش انزو فراری <noise> و می‌گه که من مشکل <noise> شما می‌دونم <noise> و بیا همکاری <noise> کنیم. <noise> فراری <noise> هم می‌گه <noise> تو چی از ماشین سر درمی‌آری <noise> برو تراکتورهاتو بساز. <noise> به فروچیو برمی‌خوره <noise> و می‌ره <noise> تا یه ماشین <noise> خفن خلق کنه <noise> و خب <noise> لامبورگینی <noise> جی‌تی رو میده بیرون <noise> به طوری که همه محو <noise> لامبورگینی <noise> می‌شن. <noise> بعدش دیگه <noise> می‌خوام <noise> بگم <noise> یه جمله‌ای رو می‌گه <noise> می‌گه شما <noise> شما فراری رو <noise> زمانی می‌خرید <noise> که به خاک کسی بشید <noise> ولی لامبورگینی رو <noise> زمانی <noise> زمانی <noise> می‌خرید که کسی شدید. <noise> بعد دیگه <noise> دیگه <noise> خب لامبورگینی <noise> بعدی <noise> کانتاش <noise> بعدی دیابلو <noise> بعد مورسیه‌لاگو <noise> بعد اون تادور <noise> هوراکان <noise> و خب کلی لامبورگینی‌های <noise> دیگه رو می‌سازه <noise> و الان <noise> کلی خاطرخواه داره <noise> و همین دیگه. <noise> خدا حافظ. <noise>"
					)
				},
				content {
					text("سلام پسر توانمندم 🌹\nموضوع کنفرانس درباره تاریخچه لامبورگینی 👌\nوماشین های سوپر اسپورت و خفنی بسازه👌\n لامبورگینی فامیل تاسیس کننده این شرکته😊 \n اسم کاملش فراچی لامبورگینی کار این مرد کشاورزیه 🌸 \n لامبورگینی باری کشور ایتالیا هست \n در ده ۶۰ میلادی ایتالیا در کشاوری ضعیف بود \n و فرانچی کار بزرگی انجام داد و تراکتور موخاس بسازه 🌹😊 \n عجب پشتکاری داشته🌸🌹 \n ممنون از اطلاعات خوبی که بهمون دادی 👌👏 \n رعایت شیوه ✅")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text("سلام. روزتون بخیر. <noise> همتا هستم <noise> با شیوه نمره یک. <noise> چالش یکشنبه. <noise> نظر و برداشتم اینه که <noise> توی هر کاری اگه <noise> زیاده‌روی کنیم <noise> خودمون در نهایت <noise> آسیب می‌بینیم <noise> و ضررش فقط <noise> به خودمون می‌رسه <noise> و <noise> کارهایی که توی <noise> اون مدت زمان <noise> انجام دادیم <noise> بی‌ارزش جلوه می‌شه. <noise> و خب <noise> هر چیزی زیاده‌اش خوب نیست و باید <noise> تعادل رو رعایت کنیم. <noise> و اینکه <noise> در کل سعی کنیم که <noise> توی کارها <noise> زیاده‌روی <noise> نکنیم. <noise> چون <noise> در نهایت فقط <noise> خودمونو <noise> کوچیک می‌کنیم و <noise> اذیت می‌شیم. <noise> و اینکه همین دیگه. <noise>")
				},
				content {
					text("feedback: سلام دختر نازم 🌹\nشیوه کشیده :۱\nنظر و برداشتتون اینکه هر کاری اگه زیاده روی کنیم\nدر نهایت آسیب می‌بینیم 👏👏\nکارهایی که تو اون مدت زمان انجام دادیم بی ارزش میشه و باید تعادل رو رعایت کنیم👏\nو توی کارها زیاده روی نکنیم 👌\nرعایت شیوه :✅")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text("سلام و درود. <noise> به <noise> همگی. <noise> امیدوارم که <noise> حالتون خوب باشه. <noise> خب من می‌خوام که <noise> یه کنفرانس <noise> راجع به <noise> دیروز <noise> بدم. <noise> خب دیروز صبح <noise> ما با دوستم <noise> رفتیم <noise> یکی از <noise> کافه‌های <noise> بافت <noise> تاریخی <noise> یزد <noise> برای <noise> برگزاری <noise> همون جلسه درمانیمون <noise> توی <noise> یه کافه دیگه. یعنی توی کافه <noise> اینو می‌خواستم بگم توی محیط دیگه. <noise> خب رفتیم <noise> اونجا <noise> و <noise> ناهار <noise> ناهارمون <noise> اونجا همون مثل <noise> کلینیک <noise> همون روندو ادامه دادیم <noise> و یه حس از جدایی <noise> کاملاً انجام دادیم <noise> و رعایت <noise> شیوه‌ها اینا <noise> دیگه <noise> تازه رو به <noise> گروه جدید بود <noise> و هم خوش گذشت <noise> در کل خیلی خوب بود. <noise> خدانگهدار. <noise>")
				},
				content {
					text("feedback: سلام پسر شگفت انگیزم❤️\nمیخواهی کنفرانس درمورد روزمره دیروز بگی👌\nبا آقای دکتر رفتید یکی کافه ی بافت تاریخی یزد😍 \n برای برگزاری جلسه ی درمانی \n رفتید کافه 🌸 \n به به چه عالی حتمن  خیلی خوش گذشته 😊  \n حساسیت زدایی هم انجام دادین 👌 \n ممنون آرش جان \n\n رعایت شیوه✅")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text(
						"سلام به همه دوستان. <noise> ظهرتون بخیر. <noise> علی مشایی هستم و جواب چالش <noise> سه‌شنبه. شیوه‌ام هم تکرار <noise> تکرار کازب. <noise> نظر و برداشت تو <noise> خب ما که حالا داریم چیزی که می‌بینیم حالا ضرب‌المثل که می‌گه <noise> دو قورت و نیمش هم باقیه. <noise> باقیه‌ست. <noise> و خب مثلاً <noise> از چی می‌گن؟ توقع زیاد داشتن. مثلاً <noise> ما ما برای یکی مثلاً این همه کار خوب کردیم. بازم از همون طلبکاره. <noise> یا اینکه مثلاً یه نفر <noise> ما رو با حرف‌هاش <noise> ما رو اینکه می‌شه برای سوال دو. حالا الان گفتم. <noise> با حرف‌هاش مثلاً ما رو ناراحت کرده <noise> و توقع داره که ما سریع اونو ببخشیم و اینا. <noise> کجاها استفاده می‌شه؟ <noise> برای اینکه گفتم برای افرادی که توقع زیاد دارن. بی‌جا نه‌ها. زیاد. <noise> توقع بی‌جا و زیاد خیلی فرق داره. <noise> و داستانشو بخونید و برامون تعریف کنید. <noise> حالا چیزی که من توی گوگل دیدم حالا می‌خوام خلاصه بگم. <noise> یه روزی یه روزی حضرت سلیمان <noise> می‌خواد یه مهمانی بگیره که کل <noise> کل حیوونای جنگلو دعوت کنه. <noise> خب می‌دونیم دیگه حضرت سلیمان خب می‌تونست با می‌تونست با حیوانات حرف بزنه. <noise> بعدش خب <noise> بعد <noise> خب این به خدا می‌گه که آره من می‌خوام دعوت کنم و اینا <noise> و آره. <noise> خدا هم می‌گه هیچ‌کس نمی‌تونه اینا رو سیر کنه جز خود من. <noise> ولی خب حضرت سلیمان زیر بار نمی‌ره و می‌گه نه من می‌خوام اینا رو دعوت کنم <noise> و آره. <noise> بعد خدا هم می‌گه باشه. حضرت سلیمان دعوت می‌کنه همه رو. <noise> بعد وقتی موقع مثلاً غذا می‌شه یه دونه ماهی گنده حالا ما می‌گیم کوسه یه چیزی <noise> همه همه همه غذاها رو می‌خوره. <noise> بعد <noise> حضرت سلیمان می‌گه مگه تو در روز چقدر غذا می‌خوری که الان همه رو خوردی؟ <noise> می‌گه من در روز سه تا وعده دارم. <noise> هر وعده‌ام هم سه تا قورت. تازه تازه این نیم این نیم قورتش بوده. <noise> و و و <noise> و دو و دو قورت و نیمم هم هنوز باقیه. <noise> اینجا دیگه اومدن که ضرب‌المثل ساختن که <noise> دو قورت و نیمش هنوز باقیه. یعنی هنوز <noise> این همه مثلاً غذا رو هم خورده می‌خواد باز. <noise> و همین دیگه. خب خدانگهدار. <noise>"
					)
				},
				content {
					text("feedback: سلام عزیزم  🧡\nامیدوارم حالت عالی باشه 🌻\n\nخیلی قشنگ  چالش رو توضیح دادی 🥰\nدر مورد اینکه  زیاده روی در هرکاری  خوب نیست 👌\nمثلاً  اگر برای کاری  تلاش زیادی کردی و جواب نگرفتی ، نا امید نشی 😇\nو اینکه  نباید  توقع زیادی  از دیگران داشت  و ....\n\nرعایت شیوه 👏🏻✅\n\nاز شنیدن صدات خوشحال شدم  😊\nخوش بگذره 🧡\n")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text(
						"سلام. اومدم با یه یه فردی حساسیت جدایی انجام بدم. خب. <noise> به نظر <noise> لوکنت <noise> لوکنت <noise> به نظر من یک <noise> مشکل تو حرف زدنه. <noise> این لوکنت چیزیه که در سر مغز دستور میده که یعنی لوکنت به مغز دستور میده که ما لوکنت <noise> و ما لوکنت می‌کنیم. خب. <noise> یعنی به غیر از چی چیز؟ <noise> به غیر از اینکه مغز دستور میده. <noise> شما چی چیزی نداره؟ مثلاً <noise> فقط دستور مغزیه که شما براتون لوکنت ایجاد می‌شه؟ ربط به زبونه. <noise> لوکنت به <noise> یعنی <noise> لوکنت به مغز دستور میده <noise> و مغز هم <noise> آها پس یعنی اگر که شما می‌تونی که چه‌کار بکنی؟ اگر شما می‌تونی که <noise> مثلاً فکر تو اصلاح بکنی می‌تونی لوکنتت هم رفع اصلاح کنی. باز مثلاً با شیوه‌ای که داریم <noise> تمرین کنیم تا مغزمون به این <noise> بدون لوکنت صحبت کردن شیوه کنه بدون لوکنت. خب. هی عادت کنه تا مغزمون دیگه عادت کنه و لوکنت دیگه بره کم‌کم. <noise> آها یعنی اینکه مثلاً تو میای <noise> مثلاً من یه عادت اشتباهی تو زندگیم دارم. مثلاً <noise> صبح دیر از خواب بیدار می‌شم. میام به مغزم هی مغزمو عادتش میدم که این دیگه <noise> تو زود از خواب بلند شو. زود از خواب بلند شدن یه چیزای خوبی داره. لوکنت هم همین‌جوریه. تو باید فکرت رو <noise> اصلاح کنی تو فکرت که می‌تونی چی‌کار بکنی. خب. <noise> حالا چطوری می‌تونی با چه شیوه‌ای می‌تونه این کارو بکنه؟ سه تا شیوه داریم. یا بیشتره؟ من فقط سه تاشو بلدم. خب. یکی روباتی که مثلاً می‌خوایم بگیم سلام. می‌تونه بگه سلام. آها یعنی مثل بخش بخش بگه. <noise> یکی کشیده. بگیم سلاااام. <noise> آها. و یکی دیگه‌اش هم اینکه اصلاح لوکنت. وقتی لوکنت می‌کنیم <noise> مثلاً می‌گیم س س سلام. برگردیم و دوباره درستش کنیم. <noise> آها یعنی تو کلمه رو اشتباه گفتی <noise> به خودتو ملزم کنی که من می‌خوام این کلمه رو درست بگم. حالا که می‌خوام کلمه رو درست بگم برمی‌گردم <noise> و این کلمه رو درستشو می‌گم. <noise> به خودت این موقع تو این اصلاحی بهت فشار نمیاد؟ <noise> نه فشار بهتر نیست که از اول مثلاً یکی از شیوه‌های روباتی یا کشیده صحبت کردنو انتخاب کنی و از همون روش استفاده کنی؟ <noise> اصلاح لوکنت من راحته. با شیوه صحبت کنیم <noise> یک کمکی <noise> ها جلوی بقیه خواسته باشه صحبت کنیم. چون من یه شاگردی دارم <noise> به شیوه روباتیک صحبت می‌کنه. <noise> الان که <noise> چی شد همین‌جوری صحبت می‌کنه. مثلاً اونم رفته اصلاح کرده و یاد گرفته و الان وقتی می‌خواد حرف بزنه <noise> آروم آروم و شمرده و مثل همون که تو می‌گی روباتی صحبت می‌کنه. <noise> و همین اصلاح لوکنت که مثلاً من دارم باهاش صحبت می‌کنم یک لوکنت می‌کنه من بهش می‌گم برگردم و کلمه رو یا دوباره بگی. <noise> بله. جالب بود. <noise> خیلی چیز خوبی بود. خب یا یادمونه که من چه اتفاقی افتاد که من لوکنت گرفتم؟ <noise> نه. <noise> چون خیلی من و تو ارتباط اینقدر نزدیکی نداشتیم که من می‌تونم که <noise> یه یادمه یه استرسی بهت ایجاد شد. بعدها مامانت برام گفت. ولی نه دقیق یادم نیست. وقتی دو سال اومد خونه‌مون <noise> یه از نمی‌دونم یه هفته بعدش من لوکنتم شروع شد <noise> و بعد رفتم پیش یه دکتری که رایحه دوبار اومد رفت تا که تا پنج سالم که بود رفتم آها. <noise> و از پنج سالم رفتم پیش آقای حسینی هستم. همون که می‌رم الان. آره. تا قبل کرونا ادامه دادم. <noise> بعدش ولش کردم <noise> و دوباره تا اسفند شروع کردم <noise> درجه لوکنتم تقریباً رو ۹ بود الان رو ۳ و ۴. <noise> خیلی فرق کردی. <noise> اول اول خیلی بد بودی. بعداش دیگه خیلی بهتر شده بودی. <noise> ولی حالا خود دیگه خیلی خوب شده. به نظر من اصلاً لوکنت نداری. مگه خود داری یا نه؟ بعضی وقتا لوکنت <noise> بعضی وقتا استرس هم تاثیر داره رو لوکنت؟ <noise> خیلی جاها استرس تاثیر داره. مخصوصاً مثلاً تو کلاس و اینا که خواسته باشی یه چیزی رو یه دفعه‌ای <noise> بپرسی اینا. بله. لوکنت <noise> و دیگه همین. مرسی. قربونت. انشالله که خوب و خوش باشی. خدانگهدار. <noise>"
					)
				},
				content {
					text("feedback: سلام  عزیزم🧡\nاومدی تا با یه فردی حساسیت زدایی  انجام بدی👌\nلکنت یه مشکل تو حرف زدنه 🥰\nو ربطی به  زبان نداره  و فقط دستور مغزه 🧠\nاگر  بتونی  فکرت رو  اصلاح کنی، میتونی لکنتت  رو اصلاح کنی🤩\nو شیوه ها رو درست انجام بدی ، مغزت به  صحبت کردن بدون لکنت عادت  می کنه 💪🏻\nمثل  بخش بخش گفتن  ، کشیده گفتن  و اصلاح لکنت  👌\nیعنی  اگر کلمه ای رو اشتباه گفتی  ، خودت رو ملزم کنی  که کلمه رو درست بگی  و  برگردی  و دوباره درستش رو بگی ✅\nو  اینکه  تو این اصلاح  به خودت فشار نیاری 😊\nبهتره  از همون اول یکی از شیوه ها رو انتخاب کنی  و همون رو ادامه بدی 👏🏻\nو سعی کن با شیوه صحبت کنی 🤗\n\nآفرین  عزیزم، خیلی خوب بود👏🏼👏🏼👏🏼\nبه امیدروانی گفتار✌✌🍂\n")
				},
				content {
					text("transcribed practical voice: ")
				},
				content {
					text("سلام. چالش یکشنبه. خب اینایی که خودتون می‌دونین. من فقط می‌گم اینا. <noise> یه دونه بزرگسال دیگه‌ای بود که اعتماد به نفس نداشت و حتی دوست نداشت عکس بگیره و حتی خودشو تو آیینه تو آیینه و عکس ببینه. <noise> قیافه‌های ظاهری رو می‌شو درست <noise> رو درست کرد. <noise> ولی سازه‌های مغز تغییر ولی اون سازه‌های مغزی <noise> اون مغز تغییر تغییر اول سخته. <noise> به‌خصوص در سن هشت سالگی به بالا. <noise> و انشا خوندن باعث می‌شه که یه گام بزرگ گام بزرگ برای <noise> درمان لوکنت برداریم. برای افشاسازی <noise> باید روزی یه محیط رو انتخاب یه روز محیط رو <noise> انتخاب کنیم که افشاسازی کنیم. <noise> و که بتونه و بتونه بیرون به راحتی حساسیت جدایی رو انجام بده. <noise> که و بگه که من لوکنت ندارم. <noise> خب بچه‌ها خیلی ممنون که واسه من تا اینجا گوش دادید. <noise>")
				},
				content {
					text("سلاام دخترم 🥰\n\nچالش یکشنبه 🌈\nدرمورد چالش یکشنبه صحبت کردی 👏🏻\nک مشخص شد ویس رو ب خوبی گوش دادی 👏🏻👏🏻 \n و برداشت خوبی داشتی 🥰 \n ممنون عزیزم 🥰 \n دو روزه هم ک فکر کنم به خاطر تثبیت، شیوه نداری 🤩")
				},
			) + loadableList
			val chat = model.startChat(chatHist)
			Log.w("TAG", "contents: ${_uiState.value.voice}")
			val generatedResponse =
				chat.sendMessage(getEnhancedResponse())
			voice = (fromJson<Voice>(generatedResponse.text) ?: return).let { voice ->
				val transcriptions =
					chatHist.map { content -> content.parts[0].asTextOrNull() }.let { chatTexts ->
						chatTexts.filterIndexed { index, _ ->
							if (index + 1 <= chatTexts.lastIndex) chatTexts[index + 1]?.contains(
								"feedback:"
							) == true else false
						}
					}
				_uiState.value.voice.copy(response = voice.response.let {
					if (it?.transcription in transcriptions) {
						it?.copy(transcription = null)
					} else {
						it
					}
				})
			}
			Log.i("TAG", "VERTO ${generatedResponse.text}")
		} catch (e: ResponseStoppedException) {
			//generateFeedback()
			e.printStackTrace()
			return
		} catch (e: Exception) {
			e.printStackTrace()
			voice = _uiState.value.voice.copy(error = e.message)
		}
	}

	private fun getEnhancedResponse(forHistory: Boolean = false) =
		when (uiState.value.voice.response?.feedbackOfFeedback) {
			BaseApplication.FeedbackOfFeedback.IncorrectOrIncomplete -> if (forHistory)
				"This feedback was so wrong or incomplete.\n"
			else
				"The previous feedback was wrong or incomplete, So Give my voice a brief feedback with highest accuracy."

			BaseApplication.FeedbackOfFeedback.TooLargeResponse -> if (forHistory)
				"This feedback was too long and wasn't human readable.\n"
			else
				"The previous feedback was too long, So Summarize my audio feedback further."

			else -> "Analyze my voice. Transcribe that. Give my voice a brief feedback with desired emojis and highest accuracy."
		}

	private fun uploadVoiceToCloud() {
		ApiClient.saltechAi.uploadVoice(
			apiKey = wantedApiKey,

			_uiState.value.voice.selectedFile!!.let {
				val progressiveRequestBody =
					ir.saltech.sokhanyar.util.ProgressRequestBody(
						it,
						"audio",
						object : ir.saltech.sokhanyar.util.ProgressRequestBody.UploadCallbacks {
							override fun onProgressUpdate(percentage: Float) {
								voice = _uiState.value.voice.copy(progress = percentage)
							}

							override fun onError() {
								voice = _uiState.value.voice.copy(progress = null)
							}

							override fun onFinish() {
								voice = _uiState.value.voice.copy(progress = 100f)
							}

						})
				MultipartBody.Part.createFormData(
					"file",
					_uiState.value.voice.selectedFile!!.name,
					progressiveRequestBody
				)
			}).call(object :
			ApiCallback<Voice> {
			override fun onSuccessful(responseObject: Voice?) {
				if (responseObject?.serverFile == null) {
					voice = _uiState.value.voice.copy(error = "File not uploaded because of null!")
					return
				}
				voice = _uiState.value.voice.copy(serverFile = responseObject.serverFile)
				Log.i("TAG", "FILE UPLOADED SUCCESSFULLY: $responseObject")
				viewModelScope.launch {
					generateFeedback()
				}
			}

			override fun onFailure(response: ErrorResponse?, t: Throwable?) {
				t?.printStackTrace()
				Log.e("TAG", "ERROR OCCURRED: ${t?.message} | RESPONSE ERROR $response")
				voice = _uiState.value.voice.copy(error = response?.detail?.message ?: t?.message)
			}

		})
	}

	private fun handleIncomingAudio(audioTempFile: File) {
		voice = _uiState.value.voice.copy(selectedFile = audioTempFile)
	}

	fun handleAudioFileIntent(context: Context, intent: Intent) {
		if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("audio/") == true) {
			val audioUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
			} else {
				@Suppress("DEPRECATION")
				intent.getParcelableExtra(Intent.EXTRA_STREAM)
			}
			audioUri?.let {
				val tempFile = File.createTempFile("audio", ".tmp", context.cacheDir)
				context.contentResolver.openInputStream(audioUri)?.use { input ->
					tempFile.outputStream().use { output ->
						input.copyTo(output)
					}
				}
				handleIncomingAudio(tempFile)
			}
		}
	}

	fun pauseAudioPlayer() {
		if (_uiState.value.mediaPlayer != null) {
			if (_uiState.value.mediaPlayer!!.isPlaying) {
				_uiState.value.mediaPlayer!!.pause()
			}
		}
	}

	fun initAudioPlayer() {
		try {
			mediaPlayer =
				MediaPlayer.create(
					context,
					_uiState.value.voice.selectedFile?.toUri()
				)
			mediaPlayer?.prepare()
			mediaPlayer?.isLooping = false
		} catch (e: Exception) {
			e.printStackTrace()
			Toast.makeText(context, "ناتوانی در آماده سازی ویس!", Toast.LENGTH_SHORT).show()
		}
	}

	fun destroyAudioPlayer() {
		_uiState.value.voice.selectedFile?.delete()
		mediaPlayer?.stop()
		mediaPlayer = null
	}
}