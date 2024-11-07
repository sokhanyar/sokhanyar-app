package ir.saltech.myapps.stutter.ui.activity

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.ui.theme.AppTheme
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.model.VoiceAnalyzeViewModel
import ir.saltech.myapps.stutter.util.RecursiveFileObserver
import ir.saltech.myapps.stutter.util.toDurationMinuteSecond
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Launcher(
    snackBar: SnackbarHostState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    voiceAnalyzeViewModel: VoiceAnalyzeViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by voiceAnalyzeViewModel.uiState.collectAsState()
    var fileObserver: RecursiveFileObserver? by remember {
        mutableStateOf(null)
    }
    var mediaPlayer: MediaPlayer? by remember {
        mutableStateOf(null)
    }
    var seekValue by remember {
        mutableFloatStateOf(mediaPlayer?.currentPosition?.toFloat() ?: 0f)
    }
    var isThreadRunning by remember {
        mutableStateOf(false)
    }
    var isMediaPlaying by remember {
        mutableStateOf(false)
    }
    val playThread = Thread {
        run {
            var currentPosition = mediaPlayer!!.currentPosition
            val total = mediaPlayer!!.duration
            while (currentPosition < total) {
                try {
                    if (isMediaPlaying) {
                        currentPosition = mediaPlayer!!.currentPosition
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
        fileObserver = voiceAnalyzeViewModel.initialVoiceObserving()
        fileObserver!!.startWatching()
    }
    if (fileObserver != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "تحلیلگر صوت سخن یار",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            if (uiState.voice.selectedFile == null) {
                Text(
                    "برای آپلود فایل ویس، لطفاً در ایتا، فایلی که میخواهید بازخورد دهید را بارگیری نمایید (فقط یک فایل در لحظه).\nپس از بارگیری فایل، برای آپلود و پردازش به اپلیکیشن بازگردید.\n\nمن منتظر دریافت فایل هستم ...",
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
                    mediaPlayer =
                        MediaPlayer.create(
                            voiceAnalyzeViewModel.context,
                            uiState.voice.selectedFile?.toUri()
                        )
                    mediaPlayer?.setOnCompletionListener {
                        isMediaPlaying = false
                    }
                    mediaPlayer?.isLooping = false
                    voiceAnalyzeViewModel.startVoiceAnalyzing()
                    fileObserver!!.stopWatching()
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp), text= "${uiState.voice.selectedFile?.name}  ||  ${(seekValue.toInt() * 1000).toDurationMinuteSecond()}  -  ${(mediaPlayer ?: return@Card).duration.toDurationMinuteSecond()}", style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr), textAlign = TextAlign.Start)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(value = seekValue, onValueChange = { seekValue = it },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(16.dp),
                            valueRange = 0f..((mediaPlayer
                                ?: return@Card).duration / 1000).toFloat(),
                            onValueChangeFinished = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    mediaPlayer?.seekTo(
                                        (seekValue * 1000).toLong(),
                                        MediaPlayer.SEEK_PREVIOUS_SYNC
                                    )
                                } else {
                                    mediaPlayer?.seekTo((seekValue * 1000).toInt())
                                }
                            }
                        )
                        FilledTonalIconButton (onClick = {
                            if (isMediaPlaying) {
                                mediaPlayer?.pause(); isMediaPlaying = false
                            } else {
                                isMediaPlaying = true; mediaPlayer?.start(); if (!isThreadRunning) {
                                    playThread.start(); isThreadRunning = true
                                }
                            }
                        }) {
                            Icon(if (isMediaPlaying) painterResource(R.drawable.baseline_pause_24) else painterResource(R.drawable.baseline_play_arrow_24), "Play Or Pause")
                        }
                    }
                }
                if (uiState.voice.response?.feedback == null && uiState.voice.error == null) {
                    Column(modifier = Modifier.height(IntrinsicSize.Min), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (uiState.voice.serverFile == null) "فایل در حال آپلود می باشد ..." else "فایل در حال پردازش می باشد.")
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator()
                    }
                } else if (uiState.voice.error == null) {
                    Text(uiState.voice.response?.feedback ?: "خطا در بازیابی اطلاعات!!")
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text("خطایی در حین بررسی ویس رخ داد!\n${uiState.voice.error}")
                }
            }
        }
    }
}

