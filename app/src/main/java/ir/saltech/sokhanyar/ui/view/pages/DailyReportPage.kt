package ir.saltech.sokhanyar.ui.view.pages

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.Constants.MAX_OF_DAILY_REPORT_PAGES
import ir.saltech.sokhanyar.BaseApplication.Constants.MAX_OF_NAME_CHARS
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.reports.DailyReport
import ir.saltech.sokhanyar.ui.state.MainUiState
import ir.saltech.sokhanyar.ui.view.components.AiAdvice
import ir.saltech.sokhanyar.ui.view.components.LockedDirection
import ir.saltech.sokhanyar.ui.view.components.MethodUsageObject
import ir.saltech.sokhanyar.ui.view.components.MinimalHelpText
import ir.saltech.sokhanyar.ui.view.components.SelfSatisfactionLayout
import ir.saltech.sokhanyar.ui.view.components.StutterSeverityRatingLayout
import ir.saltech.sokhanyar.ui.view.components.TextFieldLayout
import ir.saltech.sokhanyar.ui.view.model.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun DailyReportPage(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    snackBar: SnackbarHostState,
    mainViewModel: MainViewModel = viewModel()
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val defaultDailyReport: DailyReport by remember { mutableStateOf(uiState.dailyReport.copy(user = uiState.user)) }
    var pageCounter by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var effectSide: BaseApplication.EffectSide by remember { mutableStateOf(BaseApplication.EffectSide.Unknown) }
    var dailyReport: DailyReport by remember { mutableStateOf(defaultDailyReport) }
    Log.d("TAG", "state daily report: $dailyReport, uiState daily report ${uiState.dailyReport}")
    mainViewModel.dailyReport = dailyReport
    LockedDirection(LayoutDirection.Rtl) {
        AnimatedVisibility(isFinished, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val result: String? by remember { mutableStateOf(uiState.dailyReport.result) }
                AiAdvice(BaseApplication.ReportType.Daily, uiState)
                Spacer(modifier = Modifier.height(16.dp))
                Text(".: برای اشتراک فرم، روی اون ضربه بزنید :.", color = MaterialTheme.colorScheme.secondary)
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
                                    "ارسال گزارش روزانه"
                                )
                            )
                            //clipboardManager.setText(AnnotatedString(result, ParagraphStyle()))
                        },
                    text = result ?: "چیشده؟!",
                    style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl),
                    textAlign = TextAlign.Right
                )
            }
        }
        AnimatedVisibility(!isFinished, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = with(density) {
                        WindowInsets.ime.getBottom(density).toDp()
                    }), // For bringing up the layout to visible all of views
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
                        text = "گزارش روزانه",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    IconButton(modifier = Modifier.alpha(0f), onClick = {
                        Toast.makeText(
                            mainViewModel.context,
                            "هیچی توش نیس! ¯\\_( ͡° ͜ʖ ͡°)_/¯",
                            Toast.LENGTH_SHORT
                        ).show()
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
                                                    methodUsage = dailyReport.methodUsage.copy(
                                                        atSchool = it
                                                    )
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
                                    valueRange = 1..720,
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
                                    valueRange = 1..100,
                                    value = dailyReport.desensitizationCount,
                                    onValueChanged = {
                                        dailyReport = dailyReport.copy(
                                            desensitizationCount = it
                                        )
                                    }
                                )
                                TextFieldLayout(
                                    title = "تعداد لکنت عمدی",
                                    valueRange = 1..100,
                                    value = dailyReport.intentionalStutteringCount,
                                    onValueChanged = {
                                        dailyReport = dailyReport.copy(
                                            intentionalStutteringCount = it
                                        )
                                    }
                                )
                                TextFieldLayout(
                                    title = "تعداد تشخیص اجتناب",
                                    valueRange = 1..100,
                                    value = dailyReport.avoidanceDetectionCount,
                                    onValueChanged = {
                                        dailyReport = dailyReport.copy(
                                            avoidanceDetectionCount = it
                                        )
                                    }
                                )
                                TextFieldLayout(
                                    title = "تعداد تماس همیاری",
                                    valueRange = 1..2,
                                    value = dailyReport.callsCount.supportingP2PCallsCount,
                                    onValueChanged = {
                                        dailyReport =
                                            dailyReport.let { dailyReport ->
                                                dailyReport.copy(
                                                    callsCount = dailyReport.callsCount.copy(
                                                        supportingP2PCallsCount = it
                                                    )
                                                )
                                            }
                                    },
                                    last = true
                                )
                                Spacer(modifier = Modifier.height(26.dp))
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
                                MinimalHelpText("فعلاً چالش ها و کنفرانس هایی که در ضبط صوت موبایل (فعلاً سامسونگ) ضبط شوند، قابل تشخیص هستند.")
                                TextFieldLayout(
                                    title = "تعداد تماس گروهی",
                                    valueRange = 1..1,
                                    value = dailyReport.callsCount.groupCallsCount,
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
                                                    voicesProperties = dailyReport.voicesProperties.let { voices ->
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
                                    //enabled = defaultDailyReport.voicesProperties.challengesCount == null
                                )
                                TextFieldLayout(
                                    title = "چالش برحسب دقیقه",
                                    valueRange = 1..90,
                                    value = dailyReport.voicesProperties.sumOfChallengesDuration,
                                    onValueChanged = {
                                        dailyReport =
                                            dailyReport.let { dailyReport ->
                                                dailyReport.copy(
                                                    voicesProperties = dailyReport.voicesProperties.copy(
                                                        sumOfChallengesDuration = it
                                                    )
                                                )
                                            }
                                    },
                                    enabled = (dailyReport.voicesProperties.challengesCount
                                        ?: 0) > 0,
                                    //enabled = defaultDailyReport.voicesProperties.sumOfChallengesDuration == null,
                                    suffix = { Text("دقیقه") }
                                )
                                TextFieldLayout(
                                    title = "کنفرانس برحسب دقیقه",
                                    valueRange = 1..90,
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
                                    last = true,
                                    //enabled = defaultDailyReport.voicesProperties.sumOfConferencesDuration == null,
                                    suffix = { Text("دقیقه") }
                                )
                                StutterSeverityRatingLayout(
                                    value = dailyReport.stutterSeverityRating ?: 0,
                                    onValueChanged = {
                                        dailyReport = dailyReport.copy(
                                            stutterSeverityRating = it
                                        )
                                    }
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
                                    value = dailyReport.user.name ?: "",
                                    onValueChange = {
                                        dailyReport = dailyReport.copy(
                                            user = uiState.user.copy(
                                                name = if (it.length in 1..MAX_OF_NAME_CHARS) it else ""
                                            )
                                        )
                                    },
                                    enabled = defaultDailyReport.user.name == null,
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
                            if (pageCounter < MAX_OF_DAILY_REPORT_PAGES - 1) {
                                pageCounter++
                            } else {
                                if (dailyReport.user.name != null) {
                                    if (dailyReport.user.name!!.isEmpty()) {
                                        dailyReport = dailyReport.copy(user = User())
                                        emptyNameError()
                                    } else {
                                        focus.clearFocus()
                                        Log.w(
                                            "TAG",
                                            "dailyReport updated! state daily report: $dailyReport, uiState daily report ${uiState.dailyReport}"
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
}
