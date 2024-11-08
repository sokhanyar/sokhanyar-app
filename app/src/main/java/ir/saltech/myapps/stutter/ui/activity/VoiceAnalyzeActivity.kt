package ir.saltech.myapps.stutter.ui.activity

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daksh.mdparserkit.core.parseMarkdown
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.ui.state.VoiceAnalyzeUiState
import ir.saltech.myapps.stutter.ui.theme.AppTheme
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.model.VoiceAnalyzeViewModel
import ir.saltech.myapps.stutter.util.RecursiveFileObserver
import ir.saltech.myapps.stutter.util.toDurationMinuteSecond
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Thread.sleep


class VoiceAnalyzeActivity : ComponentActivity() {
    private val voiceAnalyzeViewModel by viewModels<VoiceAnalyzeViewModel>()
    private var folderChooserLauncher: ActivityResultLauncher<Uri?> =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) {
                applicationContext.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                startProgram()
            } else {
                finish()
            }
        }

    private fun startProgram() {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            AppTheme {
                voiceAnalyzeViewModel.context = this
                LockedDirection(LayoutDirection.Rtl) {
                    val snackBarHostState = remember { SnackbarHostState() }
                    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    }) { innerPadding ->
                        val wantedFolder =
                            File("${Environment.getExternalStorageDirectory().absolutePath}/Eitaa/Eitaa Audio")
                        var showPermissionDialog by rememberSaveable {
                            mutableStateOf(
                                contentResolver.persistedUriPermissions.isEmpty()
                            )
                        }
                        if (showPermissionDialog) {
                            if (wantedFolder.exists()) {
                                AlertDialog(
                                    onDismissRequest = { (voiceAnalyzeViewModel.context as Activity).finish() },
                                    confirmButton = {
                                        Button(onClick = {
                                            folderChooserLauncher.launch(
                                                wantedFolder.toUri()
                                            )
                                            showPermissionDialog = false
                                        }) {
                                            Text("باشه")
                                        }
                                    },
                                    title = { Text("دسترسی به فایل های ایتا") },
                                    text = { Text("فعلاً تحلیل ویس های داخل ایتا، توسط تحلیلگر پشتیبانی می گردد؛ لذا برای دسترسی به فایلهای داخل پوشه ایتا، لازم است تا این مجوز را به سخن یار اعطا کنید.\nمسیر مورد نیاز: Eitaa/Eitaa Audio") }
                                )
                            } else {
                                AlertDialog(
                                    onDismissRequest = { (voiceAnalyzeViewModel.context as Activity).finish() },
                                    confirmButton = {
                                        Button(onClick = {
                                            (voiceAnalyzeViewModel.context as Activity).finish()
                                        }) {
                                            Text("باشه")
                                        }
                                    },
                                    title = { Text("صوتی داخل ایتا نیست!") },
                                    text = { Text("پوشه ایتا صوتی یافت نشد.. لطفاً یه فایل صوتی دلخواه داخل ایتا دانلود کنید تا پوشه ظاهر شود. و دوباره امتحان کنید.") }
                                )
                            }
                        } else {
                            Launcher(snackBar = snackBarHostState, paddingValues = innerPadding)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startProgram()
    }
}


@Composable
private fun Launcher(
    snackBar: SnackbarHostState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    voiceAnalyzeViewModel: VoiceAnalyzeViewModel = viewModel()
) {
    val uiState by voiceAnalyzeViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var fileObserver: RecursiveFileObserver? by remember {
        mutableStateOf(null)
    }
    var mediaPlayer: MediaPlayer? by remember {
        mutableStateOf(null)
    }
    var isDebugInfoWanted by remember {
        mutableStateOf(false)
    }
    var isBePatientShowed by remember {
        mutableStateOf(false)
    }
    var isFeedbackOfFeedbackShowed by remember {
        mutableStateOf(false)
    }
    var isOptionsShowed by remember {
        mutableStateOf(true)
    }
    BackHandler {
        fileObserver?.stopWatching()
        (voiceAnalyzeViewModel.context as Activity).finish()
    }
    LaunchedEffect(LocalLifecycleOwner.current) {
        fileObserver = voiceAnalyzeViewModel.initialVoiceObserving()
        fileObserver!!.startWatching()
    }
    if (fileObserver != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "تحلیلگر صوت",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 35.sp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (uiState.voice.selectedFile == null) {
                Text(
                    "برای آپلود فایل ویس، لطفاً وارد ایتا شوید و در ایتا، فایلی که میخواهید بازخورد دهید را بارگیری نمایید (فقط یک فایل در لحظه).\nپس از بارگیری فایل، برای آپلود و پردازش به اپلیکیشن بازگردید.\nبرای شروع بهتر است، محتویات پوشه Eitaa Audio رو پاک کنید.\nمن منتظر دریافت فایل هستم ...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                LaunchedEffect(
                    LocalLifecycleOwner.current
                ) {
                    voiceAnalyzeViewModel.startVoiceAnalyzing()
                    fileObserver!!.stopWatching()
                }

                if (uiState.voice.response?.feedback == null && uiState.voice.error == null) {
                    Column(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(if (uiState.voice.serverFile == null) "فایل در حال آپلود می باشد ..." else "فایل در حال پردازش می باشد.")
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.voice.serverFile == null) {
                            LinearProgressIndicator(progress = {
                                (uiState.voice.progress?.toFloat() ?: 0f) / 100f
                            })
                        } else {
                            LinearProgressIndicator()
                        }
                        LaunchedEffect(!isBePatientShowed) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                run {
                                    isBePatientShowed = true
                                }
                            }, 6000)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.align(Alignment.Start),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            AnimatedVisibility(uiState.voice.serverFile == null && uiState.voice.progress != null) {
                                Text(
                                    text = "${uiState.voice.progress!!}%",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        textDirection = TextDirection.Ltr,
                                        textAlign = TextAlign.Start
                                    ),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            AnimatedVisibility(isBePatientShowed) {
                                Text(
                                    "ممکن است مدتی طول بکشد. لطفاً منتظر بمانید...",
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                } else if (uiState.voice.error == null) {
                    Text(
                        ".: برای کپی، روی متن کلیک کنید :.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        parseMarkdown(
                            uiState.voice.response?.feedback
                                ?: "خطا در بازیابی اطلاعات!!\nلطفاً مجدداً امتحان کنید."
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable {
                                clipboardManager.setText(
                                    AnnotatedString(
                                        uiState.voice.response?.feedback ?: "¯\\_(ツ)_/¯",
                                        ParagraphStyle()
                                    )
                                )
                                scope.launch {
                                    snackBar.showSnackbar("متن در کلیپ بورد کپی شد.")
                                }
                            },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = TextDirection.ContentOrRtl,
                            textAlign = TextAlign.Justify,
                            lineHeight = 28.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedVisibility(isOptionsShowed) {
                        Row(modifier = Modifier.scale(0.9f)) {
                            IconButton(onClick = {
                                if (mediaPlayer != null) {
                                    if (mediaPlayer!!.isPlaying && isDebugInfoWanted) {
                                        mediaPlayer!!.pause()
                                    }
                                }
                                isDebugInfoWanted = false
                                isFeedbackOfFeedbackShowed = !isFeedbackOfFeedbackShowed
                            }) {
                                Icon(
                                    modifier = Modifier.rotate(180f),
                                    imageVector = if (isFeedbackOfFeedbackShowed) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = "Don't Like it",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(onClick = {
                                isOptionsShowed = false
                                isDebugInfoWanted = false
                                isFeedbackOfFeedbackShowed = false
                                if (mediaPlayer != null) {
                                    if (mediaPlayer!!.isPlaying && isDebugInfoWanted) {
                                        mediaPlayer!!.pause()
                                    }
                                }
                                scope.launch {
                                    snackBar.showSnackbar("این عالیست! موفق باشید!")
                                }
                            }) {
                                Icon(
                                    Icons.Outlined.ThumbUp,
                                    "Like it",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(onClick = {
                                if (mediaPlayer != null) {
                                    if (mediaPlayer!!.isPlaying && isDebugInfoWanted) {
                                        mediaPlayer!!.pause()
                                    }
                                }
                                isFeedbackOfFeedbackShowed = false
                                isDebugInfoWanted = !isDebugInfoWanted
                            }) {
                                Icon(
                                    if (isDebugInfoWanted) painterResource(R.drawable.baseline_bug_report_24) else painterResource(
                                        R.drawable.outline_bug_report_24
                                    ),
                                    "Info / Debug",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                    AnimatedVisibility(isFeedbackOfFeedbackShowed) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("چه مشکلی داشت؟")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    OutlinedButton(onClick = {
                                        voiceAnalyzeViewModel.voice = uiState.voice.let {
                                            val lastFeedback = it.response?.feedback
                                            it.copy(
                                                response = it.response?.copy(
                                                    lastFeedback = lastFeedback,
                                                    feedbackOfFeedback = BaseApplication.FeedbackOfFeedback.TooLargeResponse,
                                                    feedback = null,
                                                    transcription = it.response.transcription
                                                        ?: "نمیدونم چرا ویس نیست!!!"
                                                )
                                            )
                                        }
                                        isBePatientShowed = false
                                        isOptionsShowed = true
                                        isDebugInfoWanted = false
                                        isFeedbackOfFeedbackShowed = false
                                        voiceAnalyzeViewModel.resetOperation(false)
                                        scope.launch {
                                            snackBar.showSnackbar("متأسفیم! ویس شما مجدداً تا لحظاتی دیگر بررسی میشود. لطفاً منتظر بمانید.")
                                        }
                                    }) {
                                        Text("غلط / ناقص بود")
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    OutlinedButton(onClick = {
                                        voiceAnalyzeViewModel.voice = uiState.voice.let {
                                            val lastFeedback = it.response?.feedback
                                            it.copy(
                                                response = it.response?.copy(
                                                    lastFeedback = lastFeedback,
                                                    feedbackOfFeedback = BaseApplication.FeedbackOfFeedback.TooLargeResponse,
                                                    feedback = null,
                                                    transcription = it.response.transcription
                                                        ?: "نمیدونم چرا ویس نیست!!!"
                                                )
                                            )
                                        }
                                        isBePatientShowed = false
                                        isOptionsShowed = true
                                        isDebugInfoWanted = false
                                        isFeedbackOfFeedbackShowed = false
                                        voiceAnalyzeViewModel.resetOperation(false)
                                        scope.launch {
                                            snackBar.showSnackbar("متأسفیم! ویس شما مجدداً تا لحظاتی دیگر بررسی میشود. لطفاً منتظر بمانید.")
                                        }
                                    }) {
                                        Text("طولانی بود")
                                    }
                                }
                            }
                        }
                    }
                    AnimatedVisibility(isDebugInfoWanted) {
                        Column {
                            LaunchedEffect(LocalLifecycleOwner.current) {
                                try {
                                    mediaPlayer =
                                        MediaPlayer.create(
                                            voiceAnalyzeViewModel.context,
                                            uiState.voice.selectedFile?.toUri()
                                        )
                                    mediaPlayer?.prepare()
                                    mediaPlayer?.isLooping = false
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (mediaPlayer != null) {
                                CardMediaPlayer(mediaPlayer!!, uiState)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (uiState.voice.response?.transcription != null) {
                                Text(
                                    "--- متن صحبت های داخل ویس ---",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    uiState.voice.response?.transcription
                                        ?: "متنی برای نمایش وجود ندارد!",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDirection = TextDirection.Rtl,
                                        textAlign = TextAlign.Justify,
                                        lineHeight = 28.sp
                                    )
                                )
                            }
                        }

                    }
                } else {
                    Text("خطایی در حین بررسی ویس رخ داد!\n${uiState.voice.error}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            isBePatientShowed = false
                            isOptionsShowed = true
                            voiceAnalyzeViewModel.resetOperation()
                        }
                    ) {
                        Text("یه بار دیگه امتحان کن!")
                    }
                }
            }
        }
    }
}

@Composable
fun CardMediaPlayer(
    mediaPlayer: MediaPlayer,
    uiState: VoiceAnalyzeUiState,
    modifier: Modifier = Modifier
) {
    var seekValue by remember {
        mutableFloatStateOf(mediaPlayer.currentPosition.toFloat())
    }
    var isThreadRunning by remember {
        mutableStateOf(false)
    }
    var isMediaPlaying by remember {
        mutableStateOf(false)
    }
    val playThread = Thread {
        run {
            var currentPosition = mediaPlayer.currentPosition
            val total = mediaPlayer.duration
            while (currentPosition < total) {
                try {
                    if (isMediaPlaying) {
                        currentPosition = mediaPlayer.currentPosition
                        seekValue = (currentPosition / 1000).toFloat()
                        sleep(1000)
                    } else {
                        continue
                    }

                } catch (e: InterruptedException) {
                    return@Thread
                } catch (e: Exception) {
                    return@Thread
                }
            }
        }
    }
    LaunchedEffect(LocalLifecycleOwner.current) {
        mediaPlayer.setOnCompletionListener {
            isMediaPlaying = false
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
            text = "${uiState.voice.selectedFile?.name}  ||  ${(seekValue.toInt() * 1000).toDurationMinuteSecond()}  -  ${mediaPlayer.duration.toDurationMinuteSecond()}",
            style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
            textAlign = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 3.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(value = seekValue, onValueChange = { seekValue = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp),
                valueRange = 0f..(mediaPlayer.duration / 1000).toFloat(),
                onValueChangeFinished = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mediaPlayer.seekTo(
                            (seekValue * 1000).toLong(),
                            MediaPlayer.SEEK_PREVIOUS_SYNC
                        )
                    } else {
                        mediaPlayer.seekTo((seekValue * 1000).toInt())
                    }
                }
            )
            FilledTonalIconButton(onClick = {
                if (isMediaPlaying) {
                    mediaPlayer.pause(); isMediaPlaying = false
                } else {
                    isMediaPlaying = true; mediaPlayer.start(); if (!isThreadRunning) {
                        playThread.start(); isThreadRunning = true
                    }
                }
            }) {
                Icon(
                    if (isMediaPlaying) painterResource(R.drawable.baseline_pause_24) else painterResource(
                        R.drawable.baseline_play_arrow_24
                    ), "Play Or Pause"
                )
            }
        }
    }
}

