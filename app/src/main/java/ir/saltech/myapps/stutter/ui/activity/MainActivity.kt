package ir.saltech.myapps.stutter.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.BaseApplication.Constants.MAX_OF_DAILY_REPORT_PAGES
import ir.saltech.myapps.stutter.BaseApplication.Constants.MAX_OF_NAME_CHARS
import ir.saltech.myapps.stutter.BaseApplication.Constants.MAX_OF_WEEKLY_REPORT_PAGES
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.MenuPageItem
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.ui.theme.AppTheme
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.components.MethodUsageObject
import ir.saltech.myapps.stutter.ui.view.components.SelfSatisfactionLayout
import ir.saltech.myapps.stutter.ui.view.components.TextFieldLayout
import ir.saltech.myapps.stutter.ui.view.model.MainViewModel
import ir.saltech.myapps.stutter.ui.view.pages.MainPage
import ir.saltech.myapps.stutter.util.getGreetingBasedOnTime
import ir.saltech.myapps.stutter.util.getSumOfActivities
import ir.saltech.myapps.stutter.util.isTomorrow
import ir.saltech.myapps.stutter.util.nowDay
import ir.saltech.myapps.stutter.util.toDayReportDate
import ir.saltech.myapps.stutter.util.toJalali
import ir.saltech.myapps.stutter.util.toRegularTime
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Date
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
        android.Manifest.permission.READ_MEDIA_AUDIO
    ) else arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val mainViewModel by viewModels<MainViewModel>()

    init {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it.values.all { granted -> granted }) {
                    startProgram()
                } else {
                    exitProcess(-1)
                }
            }
    }

    private fun checkPermissions(): Boolean {
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    private fun startProgram() {
        loadPresets()
        enableEdgeToEdge()
        setContent {
            AppTheme {
                LockedDirection(LayoutDirection.Ltr) {
                    val scope = rememberCoroutineScope()
                    val snackBarHostState = remember { SnackbarHostState() }
                    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    }) { innerPadding ->
                        if (checkPermissions()) {
                            Launcher(snackBar = snackBarHostState, paddingValues = innerPadding)
                        } else {
                            permissionLauncher.launch(permissions)
                        }
                    }
                }
            }
        }
    }

    private fun loadPresets() {
        mainViewModel.context = this
        mainViewModel.generateNewMotivationText()
        mainViewModel.loadDailyReports()
        mainViewModel.loadWeeklyReports()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startProgram()
    }
}

@Composable
fun Launcher(
    snackBar: SnackbarHostState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val onPageWanted = fun(page: BaseApplication.Page) { mainViewModel.page = page }
    MainPage(
        paddingValues,
        motivationText = uiState.sentence ?: getGreetingBasedOnTime(),
        menuPageItems = listOf(
            MenuPageItem(
                iconResId = R.drawable.schedule,
                title = "گزارش هفتگی",
                onClick = { onPageWanted(BaseApplication.Page.SendWeeklyReport) },
                disabledReason = "زمان ارسال گزارش هفتگی، جمعه ها از ساعت 6 الی 22 می باشد.",
                enabled = Calendar.getInstance()[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY && Clock.System.nowDay() isTomorrow uiState.weeklyReports?.list?.lastOrNull()?.date && Clock.System.now()
                    .toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).hour in 6..22
            ),
            MenuPageItem(
                iconResId = R.drawable.planing,
                title = "گزارش روزانه",
                onClick = {
                    mainViewModel.loadVoicesProperties()
                    onPageWanted(BaseApplication.Page.SendDailyReport)
                },
                disabledReason = "زمان ارسال گزارش روزانه، از ساعت 19 الی 23 می باشد.",
                enabled = Clock.System.nowDay() isTomorrow uiState.dailyReports?.list?.lastOrNull()?.date && Clock.System.now()
                    .toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).hour in 19..23
            ),
            MenuPageItem(
                iconResId = R.drawable.podcast,
                title = "تمرین صوتی",
                comingSoon = true,
                onClick = {
                    onPageWanted(BaseApplication.Page.Practice)
                }),
            MenuPageItem(
                iconResId = R.drawable.analysis,
                title = "تحلیل تمرین",
                onClick = {
                    onPageWanted(BaseApplication.Page.AnalyzePractice)
                }),
        )
    ) { onPageWanted(it) }
    AnimatedVisibility(
        uiState.page == BaseApplication.Page.SendDailyReport,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        DailyReportLayout(modifier, uiState, snackBar)
    }
    AnimatedVisibility(
        uiState.page == BaseApplication.Page.SendWeeklyReport,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        WeeklyReportLayout(modifier, uiState, snackBar)
    }
}

@Composable
fun MainUI(
    snackBar: SnackbarHostState,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()
    //MainLayout(uiState, snackBar, modifier)
    LaunchedEffect(androidx.lifecycle.compose.LocalLifecycleOwner.current) {
        mainViewModel.generateAdvice(uiState.dailyReports?.list ?: return@LaunchedEffect)
    }
    //Text("generated text is ${uiState.advice}")
}

//
//@Composable
//fun Motivation(
//    modifier: Modifier = Modifier,
//    uiState: MainUiState,
//    mainViewModel: MainViewModel = viewModel()
//) {
//    val speech by mainViewModel.speechOutput.collectAsState()
//    if (uiState.sentence.isNotEmpty() && BaseApplication.Constants.AI_CREDITS_SHOW || BaseApplication.Constants.MOTIVATION_WITH_SPEECH) {
//        LaunchedEffect(uiState.sentence.last().delta?.content == null) {
//            val mediaPlayer = MediaPlayer()
//            repeat(10) {
//                if (speech != null) {
//                    if (!mediaPlayer.isPlaying) {
//                        Log.i("TAG", "preparing media ...")
//                        mediaPlayer.isLooping = false
//                        mediaPlayer.setDataSource(speech!!.fd)
//                        mediaPlayer.prepare()
//                        mediaPlayer.start()
//                    }
//                }
//                withContext(Dispatchers.IO) {
//                    mainViewModel.checkCredits(object : ApiCallback<Credit> {
//                        override fun onSuccessful(responseObject: Credit?) {
//                            mainViewModel.credit = responseObject
//                            Log.i("TAG", "Credit fetch: $responseObject")
//                        }
//
//                        override fun onFailure(response: ErrorResponse?, t: Throwable?) {
//                            mainViewModel.credit = null
//                            Toast.makeText(
//                                mainViewModel.context,
//                                "Error: ${response?.details}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            t?.printStackTrace()
//                        }
//                    })
//                }
//                delay(1000)
//            }
//        }
//    }
//    Log.i("TAG", "Response Showed: ${uiState.sentence.response()}")
//    Column(
//        modifier = modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            text = "انگیزشی لکنت",
//            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
//            textAlign = TextAlign.Center
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            text = if (uiState.sentence.size > 2) uiState.sentence.response() else "در جستجوی یه انگیزشی متفاوت! ... 🤔",
//            style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.ContentOrRtl),
//            textAlign = TextAlign.Justify
//        )
//        Spacer(modifier = Modifier.height(24.dp))
//        AnimatedVisibility(uiState.credit != null) {
//            Text(
//                text = "اعتبار باقی مانده (USDT): ${uiState.credit?.totalUnit}",
//                color = MaterialTheme.colorScheme.secondary,
//                style = MaterialTheme.typography.labelLarge.copy(textDirection = TextDirection.ContentOrRtl),
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}

@Composable
fun WeeklyReportLayout(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    snackBar: SnackbarHostState,
    mainViewModel: MainViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val focus = LocalFocusManager.current
    val defaultWeeklyReport: WeeklyReport? by remember { mutableStateOf(mainViewModel.getDefaultWeeklyReport()) }
    var pageCounter by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var effectSide: BaseApplication.EffectSide by remember { mutableStateOf(BaseApplication.EffectSide.Unknown) }
    var weeklyReport: WeeklyReport by remember {
        mutableStateOf(
            defaultWeeklyReport ?: uiState.weeklyReport
        )
    }
    Log.d("TAG", "state weeklyreport: $weeklyReport, uiState weeklyreport ${uiState.weeklyReport}")
    mainViewModel.weeklyReport = weeklyReport.getSumOfActivities()
    AnimatedVisibility(isFinished, enter = fadeIn(), exit = fadeOut()) {
        val result = """
            ..#گزارش_هفتگی
            ${uiState.weeklyReport.name ?: "ناشناس"}
            
            👈تعداد روز هایی که تمرینات انجام شده: ${uiState.weeklyReport.practiceDays ?: "-"}
            👈تعداد روزهای کنفرانس دادن: ${uiState.weeklyReport.voicesProperties.conferenceDaysCount ?: "-"}
            👈تعداد مجموع کنفرانس هفته بر حسب دقیقه: ${uiState.weeklyReport.voicesProperties.sumOfConferencesDuration ?: "-"}
            👈تعداد شرکت در چالش (مثلا ۳ از n): ${uiState.weeklyReport.voicesProperties.challengesCount ?: "-"}
            👈تعداد  تماس با همیار نوجوان: ${uiState.weeklyReport.callsCount.teenSupportCallsCount ?: "-"}
            👈تعداد تماس با همیار بزرگسال: ${uiState.weeklyReport.callsCount.adultSupportCallsCount ?: "-"}
            👈تعداد تماس گروهی: ${uiState.weeklyReport.callsCount.groupCallsCount ?: "-"}
            👈تعداد گزارش حساسیت زدایی هفته: ${uiState.weeklyReport.desensitizationCount ?: "-"}
            👈خلق استثنای هفته: ${uiState.weeklyReport.creationOfExceptionCount ?: "-"}
            👈تعداد ارسال گزارش روزانه درهفته: ${uiState.weeklyReport.dailyReportsCount ?: "-"}
            👈مجموع فعالیت ها: ${uiState.weeklyReport.sumOfActivities ?: 0}
            
            ◾توضیحات اضافه: ${uiState.weeklyReport.description ?: "-"}
        """.trimIndent()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        val share = Intent(Intent.ACTION_SEND)
                        share.setType("text/plain")
                        share.putExtra(Intent.EXTRA_TEXT, result)
                        mainViewModel.context.startActivity(
                            Intent.createChooser(
                                share,
                                "ارسال گزارش هفتگی"
                            )
                        )
                        //clipboardManager.setText(AnnotatedString(result, ParagraphStyle()))
                    },
                text = result,
                style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl),
                textAlign = TextAlign.Right
            )
        }
    }
    AnimatedVisibility(!isFinished, enter = fadeIn(), exit = fadeOut()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "گزارش هفتگی",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                Column {
                    AnimatedVisibility(
                        pageCounter == 0,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column {
                            TextFieldLayout(
                                title = "روزهای تمرین شده",
                                valueRange = 1..7,
                                value = weeklyReport.practiceDays,
                                onValueChanged = {
                                    weeklyReport = weeklyReport.copy(
                                        practiceDays = it
                                    )
                                },
                                //enabled = defaultWeeklyReport?.practiceDays == null
                            )
                            TextFieldLayout(
                                title = "تعداد روزهای کنفرانس",
                                valueRange = 1..7,
                                value = weeklyReport.voicesProperties.conferenceDaysCount,
                                onValueChanged = {
                                    weeklyReport =
                                        weeklyReport.let { weeklyReport ->
                                            weeklyReport.copy(
                                                voicesProperties = weeklyReport.voicesProperties.copy(
                                                    conferenceDaysCount = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultWeeklyReport?.voicesProperties?.conferenceDaysCount == null
                            )
                            TextFieldLayout(
                                title = "مجموع زمان کنفرانس ها",
                                valueRange = 1..120,
                                value = weeklyReport.voicesProperties.sumOfConferencesDuration,
                                onValueChanged = {
                                    weeklyReport =
                                        weeklyReport.let { weeklyReport ->
                                            weeklyReport.copy(
                                                voicesProperties = weeklyReport.voicesProperties.copy(
                                                    sumOfConferencesDuration = it
                                                )
                                            )
                                        }
                                },
                                suffix = { Text("دقیقه") }
                                //enabled = defaultWeeklyReport?.voicesProperties?.sumOfConferencesDuration == null
                            )
                            TextFieldLayout(
                                title = "تعداد شرکت در چالش",
                                valueRange = 1..7,
                                value = weeklyReport.voicesProperties.challengesCount,
                                onValueChanged = {
                                    weeklyReport =
                                        weeklyReport.let { weeklyReport ->
                                            weeklyReport.copy(
                                                voicesProperties = weeklyReport.voicesProperties.copy(
                                                    challengesCount = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultWeeklyReport?.voicesProperties?.challengesCount == null
                            )
                            TextFieldLayout(
                                title = "تعداد تماس همیاری نوجوان",
                                valueRange = 1..9,
                                value = weeklyReport.callsCount.teenSupportCallsCount,
                                onValueChanged = {
                                    weeklyReport =
                                        weeklyReport.let { weeklyReport ->
                                            weeklyReport.copy(
                                                callsCount = weeklyReport.callsCount.copy(
                                                    teenSupportCallsCount = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultWeeklyReport?.callsCount?.teenSupportCallsCount == null,
                                last = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                Column {
                    AnimatedVisibility(
                        pageCounter == 1,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            TextFieldLayout(
                                title = "تعداد تماس همیاری بزرگسال",
                                valueRange = 1..9,
                                value = weeklyReport.callsCount.adultSupportCallsCount,
                                onValueChanged = {
                                    weeklyReport =
                                        weeklyReport.let { weeklyReport ->
                                            weeklyReport.copy(
                                                callsCount = weeklyReport.callsCount.copy(
                                                    adultSupportCallsCount = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultWeeklyReport?.callsCount?.adultSupportCallsCount == null
                            )
                            TextFieldLayout(
                                title = "تعداد تماس گروهی",
                                valueRange = 1..2,
                                value = weeklyReport.callsCount.groupCallsCount,
                                onValueChanged = {
                                    weeklyReport =
                                        weeklyReport.let { weeklyReport ->
                                            weeklyReport.copy(
                                                callsCount = weeklyReport.callsCount.copy(
                                                    groupCallsCount = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultWeeklyReport?.callsCount?.groupCallsCount == null
                            )
                            TextFieldLayout(
                                title = "مجموع حساسیت زدایی هفته",
                                valueRange = 1..50,
                                value = weeklyReport.desensitizationCount,
                                onValueChanged = {
                                    weeklyReport = weeklyReport.copy(
                                        desensitizationCount = it
                                    )
                                },
                                //enabled = defaultWeeklyReport?.desensitizationCount == null
                            )
                            TextFieldLayout(
                                title = "تعداد خلق استثنای هفته",
                                valueRange = 1..7,
                                value = weeklyReport.creationOfExceptionCount,
                                onValueChanged = {
                                    weeklyReport = weeklyReport.copy(
                                        creationOfExceptionCount = it
                                    )
                                },
                                supportText = { Text("چند فعالیت و چالش جدید گفتاری رو تجربه کردید؟") }
                                //enabled = defaultWeeklyReport?.creationOfExceptionCount == null
                            )
                            TextFieldLayout(
                                title = "تعداد گزارش روزانه ارسال شده",
                                valueRange = 1..7,
                                value = weeklyReport.dailyReportsCount,
                                onValueChanged = {},
                                //enabled = false,
                                last = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                Column {
                    AnimatedVisibility(
                        pageCounter == 2,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val maxOfChars = 200
                            var charsCounter by remember { mutableIntStateOf(0) }
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                value = weeklyReport.name ?: "",
                                onValueChange = {
                                    weeklyReport = weeklyReport.copy(
                                        name = if (it.length in 1..MAX_OF_NAME_CHARS) it else ""
                                    )
                                },
                                enabled = defaultWeeklyReport?.name == null,
                                singleLine = true,
                                label = { Text("نام درمانجو") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                value = (weeklyReport.description ?: "").toString(),
                                onValueChange = {
                                    weeklyReport = weeklyReport.copy(
                                        description = if (it.length in 1..200) it else null
                                    )
                                    charsCounter = it.length
                                },
                                maxLines = 5,
                                minLines = 5,
                                supportingText = { Text("$maxOfChars/$charsCounter") },
                                label = { Text("توضیحات") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        effectSide =
                            BaseApplication.EffectSide.Backward; pageCounter--; focus.clearFocus()
                    },
                    enabled = pageCounter > 0
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("قبلی")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val emptyNameError = fun() {
                            scope.launch { snackBar.showSnackbar("نام درمانجو نمیتواند خالی باشد!") }
                        }
                        effectSide = BaseApplication.EffectSide.Forward
                        if (pageCounter < MAX_OF_WEEKLY_REPORT_PAGES - 1) {
                            pageCounter++
                        } else {
                            if (weeklyReport.name != null) {
                                if (weeklyReport.name!!.isEmpty()) {
                                    weeklyReport = weeklyReport.copy(name = null)
                                    emptyNameError()
                                } else {
                                    focus.clearFocus()
                                    Log.w(
                                        "TAG",
                                        "weeklyReport updated! state weeklyReport: $weeklyReport, uiState weeklyReport ${uiState.weeklyReport}"
                                    )
                                    val result = mainViewModel.saveWeeklyReport()
                                    Log.i("TAG", "Saving weekly report result: $result")
                                    isFinished = true
                                }
                            } else {
                                emptyNameError()
                            }
                        }
                    }
                ) {
                    Text(if (pageCounter < MAX_OF_WEEKLY_REPORT_PAGES - 1) "بعدی" else "اتمام")
                    if (pageCounter < MAX_OF_WEEKLY_REPORT_PAGES - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "Forward"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DailyReportLayout(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    snackBar: SnackbarHostState,
    mainViewModel: MainViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val focus = LocalFocusManager.current
    val defaultDailyReport: DailyReport by remember { mutableStateOf(uiState.dailyReport) }
    var pageCounter by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var effectSide: BaseApplication.EffectSide by remember { mutableStateOf(BaseApplication.EffectSide.Unknown) }
    var dailyReport: DailyReport by remember { mutableStateOf(uiState.dailyReport) }
    Log.d("TAG", "state dailyreport: $dailyReport, uiState dailyreport ${uiState.dailyReport}")
    mainViewModel.dailyReport = dailyReport
    AnimatedVisibility(isFinished, enter = fadeIn(), exit = fadeOut()) {
        val result = """
            📝"فرم گزارش روزانه"
            ◾️تاریخ: ${Date(uiState.dailyReport.date!!).toJalali().toDayReportDate()} 
            ◾️نام: ${(uiState.dailyReport.name ?: "").ifEmpty { "ناشناس" }}
            ☑️مدت زمان تمرین: ${uiState.dailyReport.practiceTime?.toRegularTime() ?: "-"}
            ☑️مدت زمان اجرای شیوه در انواع محیط ها👇
            بین 5 تا 15 دقیقه 👈 1 
            بین 15 تا 30 دقیقه 👈 2 
            بین 30 تا 60 دقیقه 👈 3
            بیشتر از یک ساعت 👈 4
             خانه: ${uiState.dailyReport.methodUsage.atHome ?: "-"}
             مدرسه (دانشگاه): ${uiState.dailyReport.methodUsage.atSchool ?: "-"}
             غریبه ها: ${uiState.dailyReport.methodUsage.withOthers ?: "-"}
             فامیل و آشنا: ${uiState.dailyReport.methodUsage.withFamily ?: "-"}
            ☑️تعداد حساسیت زدایی: ${uiState.dailyReport.desensitizationCount ?: "-"}
            ☑️تعداد لکنت عمدی: ${uiState.dailyReport.intentionalStutteringCount ?: "-"}
            ☑️تعداد تشخیص اجتناب: ${uiState.dailyReport.avoidanceDetectionCount ?: "-"}
            ☑️تعداد تماس همیاری: ${
            uiState.dailyReport.callsCount.let {
                val res =
                    (it.teenSupportCallsCount ?: 0) + (it.adultSupportCallsCount ?: 0); if (res == 0) "-" else res
            }
        }
            ☑️تعداد تماس گروهی: ${uiState.dailyReport.callsCount.groupCallsCount ?: "-"}
            ☑️تعداد چالش: ${uiState.dailyReport.voicesProperties.challengesCount ?: "-"}
            ☑️کنفرانس بر حسب دقیقه: ${uiState.dailyReport.voicesProperties.sumOfConferencesDuration ?: "-"}
            ☑️رضایت از خودم (1 تا 10): ${uiState.dailyReport.selfSatisfaction ?: "-"}
            توضیحات: ${uiState.dailyReport.description ?: "-"}
        """.trimIndent()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        val share = Intent(Intent.ACTION_SEND)
                        share.setType("text/plain")
                        share.putExtra(Intent.EXTRA_TEXT, result)
                        mainViewModel.context.startActivity(
                            Intent.createChooser(
                                share,
                                "ارسال گزارش روزانه"
                            )
                        )
                        //clipboardManager.setText(AnnotatedString(result, ParagraphStyle()))
                    },
                text = result,
                style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl),
                textAlign = TextAlign.Right
            )
        }
    }
    AnimatedVisibility(!isFinished, enter = fadeIn(), exit = fadeOut()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "گزارش روزانه",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                Column {
                    AnimatedVisibility(
                        pageCounter == 0,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column {
                            MethodUsageObject(title = "رعایت شیوه در خانه",
                                value = dailyReport.methodUsage.atHome,
                                onValueChanged = {
                                    dailyReport = dailyReport.let { dailyReport ->
                                        dailyReport.copy(
                                            methodUsage = dailyReport.methodUsage.copy(atHome = it)
                                        )
                                    }
                                })
                            MethodUsageObject(title = "رعایت شیوه در مدرسه (دانشگاه)",
                                value = dailyReport.methodUsage.atSchool,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                methodUsage = dailyReport.methodUsage.copy(atSchool = it)
                                            )
                                        }
                                })
                            MethodUsageObject(title = "رعایت شیوه با غریبه ها",
                                value = dailyReport.methodUsage.withOthers,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                methodUsage = dailyReport.methodUsage.copy(
                                                    withOthers = it
                                                )
                                            )
                                        }
                                })
                            MethodUsageObject(title = "رعایت شیوه با فامیل (آشنا)",
                                value = dailyReport.methodUsage.withFamily,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                methodUsage = dailyReport.methodUsage.copy(
                                                    withFamily = it
                                                )
                                            )
                                        }
                                })
                        }
                    }
                }
                Column {
                    AnimatedVisibility(
                        pageCounter == 1,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            TextFieldLayout(
                                title = "مدت زمان تمرین",
                                valueRange = 1..120,
                                value = dailyReport.practiceTime,
                                onValueChanged = {
                                    dailyReport = dailyReport.copy(
                                        practiceTime = it
                                    )
                                },
                                suffix = { Text("دقیقه") }
                            )
                            TextFieldLayout(
                                title = "تعداد حساسیت زدایی",
                                valueRange = 1..10,
                                value = dailyReport.desensitizationCount,
                                onValueChanged = {
                                    dailyReport = dailyReport.copy(
                                        desensitizationCount = it
                                    )
                                }
                            )
                            TextFieldLayout(
                                title = "تعداد لکنت عمدی",
                                valueRange = 1..50,
                                value = dailyReport.intentionalStutteringCount,
                                onValueChanged = {
                                    dailyReport = dailyReport.copy(
                                        intentionalStutteringCount = it
                                    )
                                }
                            )
                            TextFieldLayout(
                                title = "تعداد تشخیص اجتناب",
                                valueRange = 1..50,
                                value = dailyReport.avoidanceDetectionCount,
                                onValueChanged = {
                                    dailyReport = dailyReport.copy(
                                        avoidanceDetectionCount = it
                                    )
                                }
                            )
                            TextFieldLayout(
                                title = "تعداد تماس همیار نوجوان",
                                valueRange = 1..2,
                                value = dailyReport.callsCount.teenSupportCallsCount,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                callsCount = dailyReport.callsCount.copy(
                                                    teenSupportCallsCount = it
                                                )
                                            )
                                        }
                                },
                                last = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
                Column {
                    AnimatedVisibility(
                        pageCounter == 2,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            TextFieldLayout(
                                title = "تعداد تماس همیار بزرگسال",
                                valueRange = 1..2,
                                value = dailyReport.callsCount.adultSupportCallsCount,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                callsCount = dailyReport.callsCount.copy(
                                                    adultSupportCallsCount = it
                                                )
                                            )
                                        }
                                }
                            )
                            TextFieldLayout(
                                title = "تعداد تماس گروهی",
                                valueRange = 1..1,
                                value = dailyReport.callsCount.groupCallsCount,
                                last = true,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                callsCount = dailyReport.callsCount.copy(
                                                    groupCallsCount = it
                                                )
                                            )
                                        }
                                }
                            )
                            TextFieldLayout(
                                title = "تعداد چالش",
                                valueRange = 1..4,
                                value = dailyReport.voicesProperties.challengesCount,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                voicesProperties = dailyReport.voicesProperties.copy(
                                                    challengesCount = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultDailyReport.voicesProperties.challengesCount == null
                            )
                            TextFieldLayout(
                                title = "کنفرانس برحسب دقیقه",
                                valueRange = 1..30,
                                value = dailyReport.voicesProperties.sumOfConferencesDuration,
                                onValueChanged = {
                                    dailyReport =
                                        dailyReport.let { dailyReport ->
                                            dailyReport.copy(
                                                voicesProperties = dailyReport.voicesProperties.copy(
                                                    sumOfConferencesDuration = it
                                                )
                                            )
                                        }
                                },
                                //enabled = defaultDailyReport.voicesProperties.sumOfConferencesDuration == null,
                                suffix = { Text("دقیقه") }
                            )
                            SelfSatisfactionLayout(
                                value = (dailyReport.selfSatisfaction ?: 0) / 2,
                                onValueChanged = {
                                    dailyReport = dailyReport.copy(
                                        selfSatisfaction = it
                                    )
                                }
                            )
                        }
                    }
                }
                Column {
                    AnimatedVisibility(
                        pageCounter == 3,
                        enter = slideInHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> -it; else -> it
                            }
                        } + fadeIn(),
                        exit = slideOutHorizontally {
                            when (effectSide) {
                                BaseApplication.EffectSide.Forward -> it; else -> -it
                            }
                        } + fadeOut()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val maxOfChars = 200
                            var charsCounter by remember { mutableIntStateOf(0) }
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                value = dailyReport.name ?: "",
                                onValueChange = {
                                    dailyReport = dailyReport.copy(
                                        name = if (it.length in 1..MAX_OF_NAME_CHARS) it else ""
                                    )
                                },
                                enabled = defaultDailyReport.name == null,
                                singleLine = true,
                                label = { Text("نام درمانجو") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                value = (dailyReport.description ?: "").toString(),
                                onValueChange = {
                                    dailyReport = dailyReport.copy(
                                        description = if (it.length in 1..200) it else null
                                    )
                                    charsCounter = it.length
                                },
                                maxLines = 5,
                                minLines = 5,
                                supportingText = { Text("$maxOfChars/$charsCounter") },
                                label = { Text("توضیحات") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        effectSide =
                            BaseApplication.EffectSide.Backward; pageCounter--; focus.clearFocus()
                    },
                    enabled = pageCounter > 0
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("قبلی")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val emptyNameError = fun() {
                            scope.launch { snackBar.showSnackbar("نام درمانجو نمیتواند خالی باشد!") }
                        }
                        effectSide = BaseApplication.EffectSide.Forward
                        if (pageCounter < MAX_OF_DAILY_REPORT_PAGES - 1) {
                            pageCounter++
                        } else {
                            if (dailyReport.name != null) {
                                if (dailyReport.name!!.isEmpty()) {
                                    dailyReport = dailyReport.copy(name = null)
                                    emptyNameError()
                                } else {
                                    focus.clearFocus()
                                    Log.w(
                                        "TAG",
                                        "dailyReport updated! state dailyreport: $dailyReport, uiState dailyreport ${uiState.dailyReport}"
                                    )
                                    val result = mainViewModel.saveDailyReport()
                                    Log.i("TAG", "Saving daily report result: $result")
                                    isFinished = true
                                }
                            } else {
                                emptyNameError()
                            }
                        }
                    }
                ) {
                    Text(if (pageCounter < MAX_OF_DAILY_REPORT_PAGES - 1) "بعدی" else "اتمام")
                    if (pageCounter < MAX_OF_DAILY_REPORT_PAGES - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = "Forward"
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AppTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        MainUI(snackBarHostState)
    }
}
