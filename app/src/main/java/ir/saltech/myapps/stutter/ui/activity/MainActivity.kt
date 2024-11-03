package ir.saltech.myapps.stutter.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.dto.model.MenuPageItem
import ir.saltech.myapps.stutter.ui.theme.AppTheme
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.model.MainViewModel
import ir.saltech.myapps.stutter.ui.view.pages.DailyReportPage
import ir.saltech.myapps.stutter.ui.view.pages.MainPage
import ir.saltech.myapps.stutter.ui.view.pages.WeeklyReportPage
import ir.saltech.myapps.stutter.util.getGreetingBasedOnTime
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
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
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()
    BackHandler {
        if (uiState.activePages.last() == BaseApplication.Page.Home) {
            (context as Activity).finishAfterTransition()
        } else {
            uiState.activePages.removeAt(uiState.activePages.lastIndex)
            Log.i(
                "TAG",
                "current activepages: ${uiState.activePages} + ${mainViewModel.activePages}"
            )
        }
    }
    val onPageWanted = fun(page: BaseApplication.Page) { mainViewModel.activePages = uiState.activePages.apply { if (!this.contains(page)) this.add(page) }; Log.i("TAG", "current activepages: ${uiState.activePages} + ${mainViewModel.activePages}") }
    AnimatedVisibility (uiState.activePages.last() == BaseApplication.Page.Home,
        enter = fadeIn(),
        exit = fadeOut()) {
        MainPage(
            paddingValues,
            motivationText = uiState.sentence ?: getGreetingBasedOnTime(),
            menuPageItems = listOf(
                MenuPageItem(
                    iconResId = R.drawable.schedule,
                    title = "گزارش هفتگی",
                    onClick = { onPageWanted(BaseApplication.Page.SendWeeklyReport) },
//                    disabledReason = (!(Clock.System.nowDay() isTomorrow uiState.weeklyReports?.list?.lastOrNull()?.date)).let {
//                        if (it) {
//                            "فقط یک بار در روز میتوانید گزارش خود را تکمیل کنید."
//                        } else {
//                            if (Calendar.getInstance()[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY) {
//                                Clock.System.now()
//                                    .toLocalDateTime(
//                                        TimeZone.currentSystemDefault()
//                                    ).hour.let {
//                                    if (it < 6) {
//                                        "صبح بخیر! هنوز زمان ارسال گزارش هفتگی (ساعت 6 صبح) فرا نرسیده.\nمعلومه خیلی مشتاقی! 🤓"
//                                    } else if (it >= 22) {
//                                        "زمان ارسال گزارش هفتگی (ساعت 10 شب) به پایان رسیده.\nخیلی دیر شده! 😓 ولی سعی کن از هفته بعد، جبرانش کنی! 🙂"
//                                    } else {
//                                        null
//                                    }
//                                }
//                            } else {
//                                "هنوز زمان ارسال گزارش هفتگی (روز جمعه، ساعت 6 صبح) فرا نرسیده!\nصبر داشته باش! 🌞"
//                            }
//                        }
//                    }
                ),
                MenuPageItem(
                    iconResId = R.drawable.planing,
                    title = "گزارش روزانه",
                    onClick = {
                        mainViewModel.loadVoicesProperties()
                        onPageWanted(BaseApplication.Page.SendDailyReport)
                    },
//                    disabledReason = (!(Clock.System.nowDay() isTomorrow uiState.dailyReports?.list?.lastOrNull()?.date)).let {
//                        if (it) {
//                            "فقط یک بار در روز میتوانید گزارش خود را تکمیل کنید."
//                        } else {
//                            Clock.System.now()
//                                .toLocalDateTime(
//                                    TimeZone.currentSystemDefault()
//                                ).hour.let {
//                                    if (it < 19) {
//                                        "هنوز زمان ارسال گزارش روزانه (ساعت 7 شب) فرا نرسیده.\nچقد عجله داری؟؟! 😅"
//                                    } else if (it >= 23) {
//                                        "زمان ارسال گزارش روزانه (ساعت 11 شب) به پایان رسیده.\nخیلی دیر رسیدی! 😓 ولی اشکال نداره، سعی کن فردا جبران کنی! 🙂"
//                                    } else {
//                                        null
//                                    }
//                                }
//                        }
//                    }
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
    }
    AnimatedVisibility(
        uiState.activePages.last() == BaseApplication.Page.SendDailyReport,
        enter = fadeIn() + scaleIn(initialScale = 0.75f),
        exit = fadeOut()
    ) {
        DailyReportPage(modifier.padding(paddingValues), uiState, snackBar)
    }
    AnimatedVisibility(
        uiState.activePages.last() == BaseApplication.Page.SendWeeklyReport,
        enter = fadeIn() + scaleIn(initialScale = 0.75f),
        exit = fadeOut()
    ) {
        WeeklyReportPage(modifier.padding(paddingValues), uiState, snackBar)
    }
    if (uiState.activePages.last() == BaseApplication.Page.AnalyzePractice) {
        Intent(Intent.ACTION_VIEW, Uri.parse("https://saltech.ir/sokhanyar")).apply {
            startActivity(context, this, null)
            uiState.activePages.removeAt(uiState.activePages.lastIndex)
        }
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
//    LaunchedEffect(androidx.lifecycle.compose.LocalLifecycleOwner.current) {
//        mainViewModel.generateAdvice(uiState.dailyReports?.list ?: return@LaunchedEffect)
//    }
    //Text("generated text is ${uiState.advice}")
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AppTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        MainUI(snackBarHostState)
    }
}
