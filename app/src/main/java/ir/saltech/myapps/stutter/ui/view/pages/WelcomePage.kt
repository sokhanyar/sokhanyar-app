package ir.saltech.myapps.stutter.ui.view.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.myapps.stutter.ui.state.MainUiState
import ir.saltech.myapps.stutter.ui.view.components.DropDownTextField
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.components.TextFieldLayout
import ir.saltech.myapps.stutter.ui.view.model.MainViewModel
import ir.saltech.myapps.stutter.util.validateUserInputs
import kotlinx.coroutines.launch

@Composable
fun WelcomePage(
    uiState: MainUiState,
    snackBar: SnackbarHostState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(),
    onCompleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val density = LocalDensity.current
    val keyboardHeight = with(density) { WindowInsets.ime.getBottom(density).toDp() }
    var name: String? by remember { mutableStateOf(null) }
    var age: Int? by remember { mutableStateOf(null) }
    var yearsOfStuttering: Int? by remember { mutableStateOf(null) }
    var timesOfTherapy: Int? by remember { mutableStateOf(null) }
    var stutteringType: String? by remember { mutableStateOf(null) }
    var tirednessLevel: String? by remember { mutableStateOf(null) }
    var previousStutteringSeverity: Int? by remember { mutableStateOf(null) }
    var currentStutteringSeverity: Int? by remember { mutableStateOf(null) }
    var dailyTherapyTime: String? by remember { mutableStateOf(null) }
    var currentTherapyDuration: Int? by remember { mutableStateOf(null) }
    var therapyStatus: String? by remember { mutableStateOf(null) }
    var therapyMethod: String? by remember { mutableStateOf(null) }
    var stutteringSituations: String? by remember { mutableStateOf(null) }
    var emotionalImpact: String? by remember { mutableStateOf(null) }
    var therapyGoals: String? by remember { mutableStateOf(null) }
    var previousTherapies: String? by remember { mutableStateOf(null) }
    var familyHistory: String? by remember { mutableStateOf(null) }
    var coOccurringConditions: String? by remember { mutableStateOf(null) }
    var supportSystems: String? by remember { mutableStateOf(null) }
    var escapingFromSpeechSituationsLevel: String? by remember { mutableStateOf(null) }
    var escapingFromStutteredWordLevel: String? by remember { mutableStateOf(null) }

    LockedDirection(LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                //.padding(innerPadding)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Rtl),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                    bottom = if (keyboardHeight > 0.dp) keyboardHeight else innerPadding.calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "سلام! خوش اومدی 👋",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 32.sp)
            )
            Text(
                "خوبی؟ چه خبرا؟! لطفاً برای آشنایی بیشتر من باهات، این فرم رو تکمیل کن. 📝  دمتم گرم!! 😉",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 32.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(2f)
                            .padding(bottom = 16.dp, start = 16.dp, end = 8.dp),
                        value = name ?: "",
                        onValueChange = { name = it },
                        label = { Text("اسم کامل") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall,
                        keyboardActions = KeyboardActions(onNext = {
                            focus.moveFocus(FocusDirection.Left)
                        }),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                    )
                    TextFieldLayout(
                        "سن",
                        value = age,
                        onValueChanged = { age = it },
                        valueRange = 1..80,
                        modifier = Modifier.weight(1f)
                    )
                }
                TextFieldLayout(
                    "از چند سالگی دچار لکنت شدی؟",
                    value = yearsOfStuttering,
                    onValueChanged = { yearsOfStuttering = it },
                    valueRange = 1..40
                )
                TextFieldLayout(
                    "چندبار برای درمان اقدام کردی؟",
                    value = timesOfTherapy,
                    onValueChanged = { timesOfTherapy = it },
                    valueRange = 1..10,
                    supportText = { Text("چندبار تا حالا برای درمان لکنتت، تلاش کردی؟ لزومی نداره حتماً گفتار درمان باشه!") }
                )
                DropDownTextField(
                    "نوع لکنتت چیه؟",
                    arrayOf(
                        "قفل کلمه با شدت (مکث) زیاد و حرکات اضافی",
                        "قفل کلمه با شدت (مکث) کم",
                        "تکرار حروف کلمه با شدت (مکث) زیاد",
                        "تکرار حروف کلمه با شدت (مکث) کم"
                    ),
                    selectedChoice = stutteringType,
                    onChoiceSelected = { stutteringType = it },
                    supportText = { Text("اگه الان پیش درمانگر میری، ازش بپرس ببین لکنتت از چه نوعیه.") }
                )
                DropDownTextField(
                    "چقدر از لکنت کردن خسته شدی؟",
                    arrayOf(
                        "خیلی! همیشه رو اعصابمه",
                        "هعی؛ بعضی وقتا یادم میوفته لکنت دارم",
                        "اممم؛ دیگه برام عادی شده"
                    ),
                    selectedChoice = tirednessLevel,
                    onChoiceSelected = { tirednessLevel = it },
                    supportText = { Text("یا اینجور بگیم: چقدر انگیزه داری از شرّ لکنت خلاص شی؟") }
                )
                TextFieldLayout(
                    "نمره لکنتت قبل از درمان",
                    value = previousStutteringSeverity,
                    onValueChanged = { previousStutteringSeverity = it },
                    valueRange = 0..9,
                    supportText = { Text("از 0 تا 9، درجه لکنتت ابتدای درمانت چند بود؟ از گفتاردرمانت سؤال کن. مثلاً درجه 9 یعنی لکنت های خیلی شدید و 0 هم یعنی اصلاً لکنت نداری!") }
                )
                TextFieldLayout(
                    "نمره الان لکنتت",
                    value = currentStutteringSeverity,
                    onValueChanged = { currentStutteringSeverity = it },
                    valueRange = 0..9,
                    supportText = { Text("از 0 تا 9، درجه لکنتت الان که در درمانی، چنده؟ از گفتاردرمانت سؤال کن.") }
                )
                DropDownTextField(
                    "چقدر در روز برای درمانت وقت میذاری؟",
                    arrayOf(
                        "بیشتر از اون | بیشتر از یک ساعت",
                        "حد معین | بین 30 تا 60 دقیقه",
                        "کمتر از اون | بین 10 تا 30 دقیقه"
                    ),
                    selectedChoice = dailyTherapyTime,
                    onChoiceSelected = { dailyTherapyTime = it },
                    supportText = {
                        Text("حد معین یعنی مدت زمانی که دکتر برات معین کرده تمرین کنی .. مثلا 45 دق در روز")
                    },
                )
                TextFieldLayout(
                    "طول دوره درمان فعلیت",
                    value = currentTherapyDuration,
                    onValueChanged = { currentTherapyDuration = it },
                    valueRange = 1..72,
                    suffix = { Text("ماه") },
                    supportText = { Text("دوره درمان فعلیت رو چه مدتیه شروع کردی؟\nبرحسب ماه بنویس .. اگه مثلاً 3 سال شده، میشه 36 ماه\nاگه کمتر از یه ماهه، همون 1 بزن.") }
                )
                DropDownTextField(
                    "وضعیت فعلی درمانت چیه؟",
                    arrayOf(
                        "در حال از بین بردن لکنتم",
                        "به تثبیت رسیدم و مرحله حفظ و نگهداری هستم",
                    ),
                    selectedChoice = therapyStatus,
                    onChoiceSelected = { therapyStatus = it }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = therapyMethod ?: "",
                    onValueChange = { therapyMethod = it },
                    label = { Text("شیوه درمانی فعلیت چیه؟") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    supportingText = { Text("الان اگه مثلاً از شیوه کشیده گویی استفاده می کنی یا از شیوه رباتیک؛ اسمشو بهم بگو.") },
                    keyboardActions = KeyboardActions(onNext = {
                        focus.moveFocus(FocusDirection.Down)
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = stutteringSituations ?: "",
                    onValueChange = { stutteringSituations = it },
                    label = { Text("در چه موقعیت‌هایی بیشتر لکنت می‌کنی؟") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    supportingText = { Text("مثلاً اگه میری توی مغازه و لکنتت زیاد میشه، نمیتونی حرف بزنی، بگو!") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = emotionalImpact ?: "",
                    onValueChange = { emotionalImpact = it },
                    label = { Text("تأثیر لکنت بر احساساتت چطوره؟") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    supportingText = { Text("وقتی لکنت میکنی یا میخوای لکنت کنی، چه حسی بهت دست میده؟") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = therapyGoals ?: "",
                    onValueChange = { therapyGoals = it },
                    label = { Text("هدفت از درمان چیه؟") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = false,
                    supportingText = { Text("واسه چی میخوای لکنتت خوب بشه؟") },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = previousTherapies ?: "",
                    onValueChange = { previousTherapies = it },
                    label = { Text("از چه روش‌های درمانی قبلاً استفاده کردی؟") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                DropDownTextField(
                    label = "آیا سابقه خانوادگی لکنت داری؟",
                    choices = arrayOf("بله", "خیر"),
                    selectedChoice = familyHistory,
                    onChoiceSelected = { familyHistory = it },
                    supportText = { Text("کسی هست توی خونواده یا فامیل تون که لکنت داشته، حتی خفیف؟") }
                )
                OutlinedTextField(
                    value = supportSystems ?: "",
                    onValueChange = { supportSystems = it },
                    label = { Text("حمایت خانواده یا دوستانت چطوره؟") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = false,
                    supportingText = { Text("چقدر مامان و بابا یا دوستانت، از اینکه پیگیر درمانت هستی، تشویقت میکنن؟") },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                DropDownTextField(
                    label = "چقدر از موقعیت های گفتاری فرار میکنی؟",
                    choices = arrayOf(
                        "تقریباً همیشه",
                        "بعضی وقتا",
                        "فقط از پشت تلفن میترسم",
                        "فقط از حضوری میترسم",
                        "هیچوقت فرار نمیکنم"
                    ),
                    selectedChoice = escapingFromSpeechSituationsLevel,
                    onChoiceSelected = { escapingFromSpeechSituationsLevel = it },
                    supportText = { Text("کجا و چقدر نمیخوای از ترس اینکه لکنت کنی، با کسی صحبت کنی؟ مثلاً دوست نداری تلفن جواب بدی یا بری خونه عمه، چون لکنت میکنی.") }
                )
                DropDownTextField(
                    label = "چقدر کلمات رو حین صحبتت عوض میکنی؟",
                    choices = arrayOf(
                        "بیشتر کلمات توی یه جمله",
                        "گهگاهی؛ کمتر از نصف کلمات یه جمله",
                        "هیچوقت کلمه رو تو جمله عوض نمی کنم",
                    ),
                    selectedChoice = escapingFromStutteredWordLevel,
                    onChoiceSelected = { escapingFromStutteredWordLevel = it },
                    supportText = { Text("وقتی حرف میزنی، چندتا از کلماتی که میدونی قراره لکنت کنی رو قبل از بیانش، با یه کلمه دیگه که میدونی لکنت نمیکنی جایگزین میکنی؟") }
                )
                OutlinedTextField(
                    value = coOccurringConditions ?: "",
                    onValueChange = { coOccurringConditions = it },
                    label = { Text("آیا مشکل گفتاری دیگه‌ای هم داری؟") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = false,
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    supportingText = { Text("اگه مشکل دیگه ای هم در مورد صحبت کردنت داری، به طور خیلی خلاصه بهم بگو.") }
                )
            }
            HorizontalDivider(thickness = 0.7.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "* اگر به عنوان یاریگر از این اَپ استفاده میکنید، فقط اسم و سنِتون رو بنویسید کافیه!",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium.copy(textDirection = TextDirection.Rtl),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    focus.clearFocus()
                    if (name != null && age != null && (age ?: 0) >= 8) {
                        val user = uiState.user.copy(
                            name = name,
                            age = age,
                            yearOfStartStuttering = yearsOfStuttering,
                            timesOfTherapy = timesOfTherapy,
                            stutteringType = stutteringType,
                            tirednessLevel = tirednessLevel,
                            currentStutteringSeverity = currentStutteringSeverity,
                            previousStutteringSeverity = previousStutteringSeverity,
                            dailyTherapyTime = dailyTherapyTime,
                            currentTherapyDuration = currentTherapyDuration,
                            therapyStatus = therapyStatus,
                            therapyMethod = therapyMethod,
                            stutteringSituations = stutteringSituations,
                            emotionalImpact = emotionalImpact,
                            therapyGoals = therapyGoals,
                            previousTherapies = previousTherapies,
                            familyHistory = familyHistory,
                            coOccurringConditions = coOccurringConditions,
                            supportSystems = supportSystems,
                            escapingFromSpeechSituationsLevel = escapingFromSpeechSituationsLevel,
                            escapingFromStutteredWordLevel = escapingFromStutteredWordLevel
                        )
                        if (validateUserInputs(user)) {
                            mainViewModel.user = user
                            Log.i("TAG", "user is saving: $user")
                            mainViewModel.saveUser()
                            onCompleted()
                        } else {
                            scope.launch {
                                snackBar.showSnackbar(
                                    "حالا که یکی دوتاش رو پر کردی، لطف کن بقیه اش هم پر کن. ممنون :)",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    } else {
                        scope.launch {
                            snackBar.showSnackbar(
                                "حداقل اسم و سنِت رو وارد کن",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }) {
                    Text("بزن بریم")
                }
            }
        }
    }
}
