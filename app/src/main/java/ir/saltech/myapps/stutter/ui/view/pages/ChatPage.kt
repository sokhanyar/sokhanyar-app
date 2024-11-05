package ir.saltech.myapps.stutter.ui.view.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daksh.mdparserkit.core.parseMarkdown
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import ir.saltech.myapps.stutter.BaseApplication.Greeting.greetingIcon
import ir.saltech.myapps.stutter.BaseApplication.Greeting.greetingText
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.dto.model.ai.ChatMessage
import ir.saltech.myapps.stutter.dto.model.ui.ChatActionWantedListener
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.model.MainViewModel
import ir.saltech.myapps.stutter.util.epochToHoursMinutes
import ir.saltech.myapps.stutter.util.epochToMonthDay
import kotlinx.coroutines.launch

@Composable
fun ChatPage(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    uiState: MainUiState,
    snackBar: SnackbarHostState,
    mainViewModel: MainViewModel = viewModel()
) {
    val density = LocalDensity.current
    val chatHistory by uiState.chatHistory.collectAsState()
    val chatMessagesState = rememberLazyListState()
    var message by rememberSaveable { mutableStateOf("") }
    var startOverWanted by rememberSaveable { mutableStateOf(false) }
    //Log.i("New", "New contents ${uiState.chatHistory}")
    if (startOverWanted) {
        LockedDirection(LayoutDirection.Rtl) {
            AlertDialog(
                onDismissRequest = { startOverWanted = false },
                confirmButton = {
                    Button(onClick = {
                        mainViewModel.startOverChat(); startOverWanted = false
                    }) { Text("مطمئنم") }
                },
                dismissButton = {
                    OutlinedButton(modifier = Modifier.padding(end = 8.dp),
                        onClick = { startOverWanted = false }) { Text("ولش کن") }
                },
                title = { Text("شروع مجدد گفتگو") },
                text = { Text("با این کار، همه تاریخچه گفتگوها حذف میشن و گفتگو از اول آغاز میشه.\nمطمئنی؟") },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ShaderBrush(
                    ImageShader(
                        ImageBitmap.imageResource(if (isSystemInDarkTheme()) R.drawable.chat_dark_background else R.drawable.chat_light_background),
                        tileModeX = TileMode.Mirror,
                        tileModeY = TileMode.Mirror
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = with(density) {
                    WindowInsets.ime.getBottom(
                        density
                    ).toDp()
                })
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.54f))
        ) {
//            val topFade = Brush.verticalGradient(0.7f to Color.Red, 1f to Color.Transparent)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Rtl)
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                androidx.compose.animation.AnimatedVisibility(chatHistory.contents.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = chatMessagesState,
                        reverseLayout = true
                    ) {
                        itemsIndexed(uiState.chatHistory.value.contents.filter { it.id >= 0 }
                            .reversed()) { index, message ->
                            ChatMessageBox(
                                message,
                                chatHistory.contents.filter { it.id >= 0 }.reversed(),
                                index
                            )
                        }
                    }
                    val firstItemVisible by remember {
                        derivedStateOf {
                            chatMessagesState.firstVisibleItemScrollOffset == 0 || !chatMessagesState.isScrollInProgress
                        }
                    }
                    LockedDirection(LayoutDirection.Rtl) {
                        ExtendedFloatingActionButton(
                            text = { Text("شروع مجدد") },
                            icon = {
                                Icon(
                                    painterResource(R.drawable.round_restart_alt_24),
                                    "Start over chat"
                                )
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopEnd),
                            onClick = {
                                startOverWanted = true
                            },
                            expanded = firstItemVisible
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(chatHistory.contents.isEmpty()) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(250.dp)
                            .align(Alignment.Center)
                    ) {
                        val (greeting, blur) = createRefs()
                        Card(
                            modifier = Modifier
                                .constrainAs(blur) {
                                    top.linkTo(greeting.top)
                                    bottom.linkTo(greeting.bottom)
                                    start.linkTo(greeting.start)
                                    end.linkTo(greeting.end)
                                }
                                .blur(13.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright.copy(
                                    alpha = 0.33f
                                )
                            ),
                            shape = MaterialTheme.shapes.large.copy(CornerSize(25.dp))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                DotLottieAnimation(
                                    source = DotLottieSource.Asset(greetingIcon),
                                    modifier = Modifier
                                        .size(175.dp)
                                        .padding(top = 8.dp, end = 8.dp, start = 8.dp),
                                    playMode = Mode.BOUNCE,
                                    loop = true,
                                    autoplay = true
                                )
                                Text(
                                    greetingText,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .alpha(0f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.constrainAs(greeting) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright.copy(
                                    alpha = 0.58f
                                )
                            ),
                            shape = MaterialTheme.shapes.large.copy(CornerSize(25.dp))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                DotLottieAnimation(
                                    source = DotLottieSource.Asset(greetingIcon),
                                    modifier = Modifier
                                        .size(175.dp)
                                        .padding(top = 8.dp, end = 8.dp, start = 8.dp),
                                    playMode = Mode.BOUNCE,
                                    loop = true,
                                    autoplay = true
                                )
                                Text(
                                    greetingText,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.ContentOrRtl)
                                )
                            }
                        }
                    }
                }
//                Row (modifier = Modifier.fillMaxWidth().fadingEdge(topFade).background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.85f)).align(Alignment.TopCenter), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
//                    IconButton(onClick = {}) {
//                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back To Menu")
//                    }
//                }
            }
            ChatInput(
                message,
                onMessageChanged = { message = it },
                snackBar,
                innerPadding,
                object : ChatActionWantedListener {
                    override fun onSendWanted() {
                        mainViewModel.viewModelScope.launch { chatMessagesState.scrollToItem(0) }
                        mainViewModel.generateNewMessage(message)
                        message = ""
                    }

                    override fun onStartOverWanted() {
                        mainViewModel.startOverChat()
                    }

                    override fun onScheduledSendWanted() {
                        TODO("Not yet implemented")
                    }

                }
            )
        }
    }
}

@Composable
fun ChatMessageBox(content: ChatMessage, list: List<ChatMessage> = emptyList(), index: Int = 0) {
    Column {
        if (list.isNotEmpty()) {
            if (index + 1 == list.size) {
                DayIndicator(content.createdAt.epochToMonthDay())
            } else if ((list[index].createdAt / 86400000) != (list[index + 1].createdAt / 86400000)) {
                DayIndicator(content.createdAt.epochToMonthDay())
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .align(if (content.role == "user") Alignment.BottomEnd else Alignment.BottomStart)
                    .width(IntrinsicSize.Max),
                colors = CardDefaults.cardColors(
                    containerColor = if (content.role == "user") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = if (content.role == "user") MaterialTheme.colorScheme.onTertiary else CardDefaults.cardColors().contentColor
                ),
                shape = if (content.role == "user") MaterialTheme.shapes.large.copy(
                    bottomEnd = CornerSize(
                        0
                    )
                ) else MaterialTheme.shapes.large.copy(bottomStart = CornerSize(0))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (content.content == "...") {
                        DotLottieAnimation(
                            source = DotLottieSource.Asset("loading2.json"),
                            modifier = Modifier
                                .width(150.dp)
                                .height(50.dp)
                                .scale(1.2f)
                                .padding(8.dp),
                            playMode = Mode.FORWARD,
                            loop = true,
                            autoplay = true
                        )
                    } else {
                        if (content.role == "user") {
                            ResponseText(content.content)
                        } else {
                            SelectionContainer {
                                ResponseText(content.content)
                            }
                        }
                        Text(
                            modifier = Modifier
                                .padding(top = 5.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
                                .align(if (content.role == "user") Alignment.Start else Alignment.End),
                            text = content.createdAt.epochToHoursMinutes(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (content.role == "user") MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatInput(
    message: String,
    onMessageChanged: (String) -> Unit,
    snackBar: SnackbarHostState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    actionWantedListener: ChatActionWantedListener
) {
    val focusRequester = remember { FocusRequester() }
    val density = LocalDensity.current
    val keyboardHeight = with(density) {
        WindowInsets.ime.getBottom(
            density
        ).toDp()
    }
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var emojiWanted by remember { mutableStateOf(false) }
    var previousHeight by remember { mutableStateOf(0.dp) }
    Card(
        modifier = Modifier.padding(0.dp),
        shape = MaterialTheme.shapes.large.copy(CornerSize(0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (WindowInsets.isImeVisible) 0.dp else innerPadding.calculateBottomPadding()),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                IconButton(
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .padding(bottom = 3.dp),
//                    onClick = {
//                        actionWantedListener.onStartOverWanted()
//                    },
//                    enabled = chatHistorySize >= 1
//                ) {
//                    Icon(painterResource(R.drawable.round_restart_alt_24), "Start over chat")
//                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = 3.dp),
                    onClick = {
                        emojiWanted = !emojiWanted
                        if (emojiWanted) {
                            previousHeight = keyboardHeight
                            keyboardController?.hide()
                            focus.clearFocus()
                        } else {
                            keyboardController?.show()
                            focusRequester.requestFocus()
                        }
                    }
                ) {
                    Icon(
                        painterResource(if (emojiWanted) R.drawable.outline_keyboard_24 else R.drawable.outline_emoji_emotions_24),
                        "Switch Emoji or Keyboard"
                    )
                }
                LockedDirection(LayoutDirection.Rtl) {
                    TextField(
                        message,
                        onValueChange = {
                            if (it.length <= 1500) {
                                onMessageChanged(it)
                            } else {
                                focus.clearFocus()
                                scope.launch {
                                    snackBar.showSnackbar(
                                        "حداکثر تعداد حروف، 600 تا در هر پیام می باشد!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    previousHeight = keyboardHeight
                                    emojiWanted = false
                                }
                            },
                        shape = MaterialTheme.shapes.large,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        placeholder = {
                            Text(
                                "هرچی دل تنگت میخواد، بگو!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.ContentOrRtl),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = 3.dp),
                    onClick = {
                        actionWantedListener.onSendWanted()
                    },
                    enabled = message.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, "Send Message")
                }
            }
            BackHandler(enabled = emojiWanted) {
                emojiWanted = false
            }
            if (emojiWanted) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .let {
                            if (previousHeight > 0.dp) {
                                it.height(previousHeight)
                            } else {
                                it.fillMaxHeight(0.4f)
                            }
                        },
                    factory = { context -> EmojiPickerView(context) },
                    update = {
                        it.setOnEmojiPickedListener { emoji ->
                            val cursorPosition = message.length
                            onMessageChanged(
                                message.substring(
                                    0,
                                    cursorPosition
                                ) + emoji.emoji + message.substring(cursorPosition)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DayIndicator(createdAt: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(0.93f)
            .padding(8.dp), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.215f),
                shape = MaterialTheme.shapes.small.copy(CornerSize(13.dp))
            )
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = createdAt,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.ContentOrRtl),
                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ResponseText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .widthIn(150.dp, 300.dp)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        text = parseMarkdown(text.trim()),
        textAlign = TextAlign.Start,
        lineHeight = 26.sp,
        style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.ContentOrRtl)
    )
}
