package ir.saltech.myapps.stutter.ui.view.pages

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.BaseApplication.Constants.MAX_OF_NAME_CHARS
import ir.saltech.myapps.stutter.BaseApplication.Constants.MAX_OF_WEEKLY_REPORT_PAGES
import ir.saltech.myapps.stutter.dto.model.data.general.User
import ir.saltech.myapps.stutter.dto.model.data.reports.WeeklyReport
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.ui.view.components.AiAdvice
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.components.MinimalHelpText
import ir.saltech.myapps.stutter.ui.view.components.TextFieldLayout
import ir.saltech.myapps.stutter.ui.view.model.MainViewModel
import ir.saltech.myapps.stutter.util.getSumOfActivities
import kotlinx.coroutines.launch


@Composable
fun WeeklyReportPage(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    snackBar: SnackbarHostState,
    mainViewModel: MainViewModel = viewModel()
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
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
    LockedDirection(LayoutDirection.Rtl) {
        AnimatedVisibility(isFinished, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val result by remember { mutableStateOf(uiState.weeklyReport.result) }
                Log.i("TAG", "Solution is $result")
                AiAdvice(BaseApplication.ReportType.Weekly, uiState)
                Text("برای اشتراک فرم، روی اون ضربه بزنید.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
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
                    text = result ?: "چیشده؟",
                    style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl),
                    textAlign = TextAlign.Right
                )
            }
        }
        AnimatedVisibility(!isFinished, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = with(density) { WindowInsets.ime.getBottom(density).toDp() }), // For bringing up the layout to visible all of views
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(modifier = Modifier, onClick = {
                        uiState.activePages.removeAt(uiState.activePages.lastIndex)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "گزارش هفتگی",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    IconButton(modifier = Modifier, onClick = {
                        Toast.makeText(mainViewModel.context, "هیچی توش نیس! ¯\\_( ͡° ͜ʖ ͡°)_/¯", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Items"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
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
                                MinimalHelpText("فعلاً چالش ها و کنفرانس هایی که در ضبط صوت موبایل (فعلاً سامسونگ) ضبط شوند، قابل تشخیص هستند.")
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
                                                    voicesProperties = weeklyReport.voicesProperties.let { voices ->
                                                        if ((it ?: 0) > 0) {
                                                            voices.copy(
                                                                conferenceDaysCount = it
                                                            )
                                                        } else {
                                                            voices.copy(
                                                                conferenceDaysCount = it,
                                                                sumOfConferencesDuration = null
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                    },
                                    //enabled = defaultWeeklyReport?.voicesProperties?.conferenceDaysCount == null
                                )
                                TextFieldLayout(
                                    title = "مجموع زمان کنفرانس ها",
                                    valueRange = 1..350,
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
                                    suffix = { Text("دقیقه") },
                                    enabled = (weeklyReport.voicesProperties.conferenceDaysCount ?: 0) > 0
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
                                                    voicesProperties = weeklyReport.voicesProperties.let { voices ->
                                                        if ((it ?: 0) > 0) {
                                                            voices.copy(
                                                                challengesCount = it
                                                            )
                                                        } else {
                                                            voices.copy(
                                                                challengesCount = it,
                                                                sumOfChallengesDuration = null
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                    },
                                    //enabled = defaultWeeklyReport?.voicesProperties?.challengesCount == null
                                )
                                TextFieldLayout(
                                    title = "مجموع زمان چالش ها",
                                    valueRange = 1..350,
                                    value = weeklyReport.voicesProperties.sumOfChallengesDuration,
                                    onValueChanged = {
                                        weeklyReport =
                                            weeklyReport.let { weeklyReport ->
                                                weeklyReport.copy(
                                                    voicesProperties = weeklyReport.voicesProperties.copy(
                                                        sumOfChallengesDuration = it
                                                    )
                                                )
                                            }
                                    },
                                    suffix = { Text("دقیقه") },
                                    enabled = (weeklyReport.voicesProperties.challengesCount ?: 0) > 0
                                    //enabled = defaultWeeklyReport?.voicesProperties?.sumOfConferencesDuration == null
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
                                    onValueChanged = {
                                        weeklyReport = weeklyReport.copy(
                                            dailyReportsCount = it
                                        )
                                    },
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
                                    value = weeklyReport.user.name ?: "",
                                    onValueChange = {
                                        weeklyReport = weeklyReport.copy(
                                            user = uiState.user.copy(name = if (it.length in 1..MAX_OF_NAME_CHARS) it else "")
                                        )
                                    },
                                    enabled = defaultWeeklyReport?.user?.name == null && weeklyReport.user.name == null,
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
                        .padding(bottom = 24.dp)
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
                                if (weeklyReport.user.name != null) {
                                    if (weeklyReport.user.name!!.isEmpty()) {
                                        weeklyReport = weeklyReport.copy(user = User())
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
}
