package ir.saltech.myapps.stutter.ui.view.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.isDigitsOnly
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.BaseApplication.Constants.MAX_OF_DISPLAYED_CHAR_COLLAPSE
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.ui.state.MainUiState

@Composable
fun LockedDirection(
    direction: LayoutDirection = LayoutDirection.Ltr, content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MethodUsageObject(title: String, value: Int?, onValueChanged: (Int?) -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Light,
            textDirection = TextDirection.ContentOrRtl
        ),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SegmentedButton(
            selected = value == 1,
            onClick = { onValueChanged(if (value == 1) null else 1) },
            shape = MaterialTheme.shapes.medium.copy(
                topStart = CornerSize(100.dp),
                bottomStart = CornerSize(100.dp),
                topEnd = CornerSize(0),
                bottomEnd = CornerSize(0)
            )
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = "5 تا 15 دق",
                style = MaterialTheme.typography.labelLarge.copy(textDirection = TextDirection.Rtl),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        SegmentedButton(
            selected = value == 2,
            onClick = { onValueChanged(if (value == 2) null else 2) },
            shape = MaterialTheme.shapes.medium.copy(all = CornerSize(0))
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = "15 تا 30 دق",
                style = MaterialTheme.typography.labelLarge.copy(textDirection = TextDirection.Rtl),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        SegmentedButton(
            selected = value == 3,
            onClick = { onValueChanged(if (value == 3) null else 3) },
            shape = MaterialTheme.shapes.medium.copy(all = CornerSize(0))
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = "30 تا 60 دق",
                style = MaterialTheme.typography.labelLarge.copy(textDirection = TextDirection.Rtl),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        SegmentedButton(
            selected = value == 4,
            onClick = { onValueChanged(if (value == 4) null else 4) },
            shape = MaterialTheme.shapes.medium.copy(
                topEnd = CornerSize(100.dp),
                bottomEnd = CornerSize(100.dp),
                topStart = CornerSize(0),
                bottomStart = CornerSize(0)
            )
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = "بیش از 1 س",
                style = MaterialTheme.typography.labelLarge.copy(textDirection = TextDirection.Rtl),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun DropDownTextField(
    label: String,
    choices: Array<String>,
    onChoiceSelected: (String) -> Unit,
    selectedChoice: String? = null,
    modifier: Modifier = Modifier,
    supportText: @Composable (() -> Unit)? = null
) {
    val focus = LocalFocusManager.current
    var expand by remember {
        mutableStateOf(false)
    }
    var choice: String? by remember {
        mutableStateOf(selectedChoice)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = choice ?: "",
            onValueChange = {
            },
            label = { Text(label) },
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        Log.i("TAG", "Trans is expanded: $expand")
                        expand = true
                    } else {
                        expand = false
                    }
                },
            singleLine = true,
            readOnly = true,
            supportingText = supportText,
            textStyle = MaterialTheme.typography.bodySmall
        )
        DropdownMenu(
            expand,
            onDismissRequest = { expand = false; focus.clearFocus() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.large.copy(
                CornerSize(15.dp)
            )
        ) {
            choices.forEach {
                DropdownMenuItem({ Text(it) }, onClick = {
                    choice = it
                    onChoiceSelected(it)
                    expand = false
                    focus.clearFocus()
                })
            }
        }
    }
}

@Composable
fun TextFieldLayout(
    title: String,
    valueRange: IntRange,
    value: Int?,
    onValueChanged: (Int?) -> Unit,
    last: Boolean = false,
    enabled: Boolean = true,
    suffix: (@Composable () -> Unit)? = null,
    supportText: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val focus = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = (value ?: "").toString(),
        onValueChange = {
            if (it.isDigitsOnly() && it.toIntOrNull() in valueRange) {
                onValueChanged(it.toInt())
            } else {
                onValueChanged(null)
            }
        },
        enabled = enabled,
        label = { Text(title) },
        supportingText = supportText,
        suffix = suffix,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = if (last) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = if (last) KeyboardActions(onDone = { focus.clearFocus() }) else KeyboardActions(
            onNext = { focus.moveFocus(FocusDirection.Down) })
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SelfSatisfactionLayout(value: Int, onValueChanged: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "رضـایـت از خـودم",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 21.sp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        LockedDirection {
            var score by remember { mutableFloatStateOf(value.toFloat()) }
            RatingBar(modifier = Modifier.padding(horizontal = 16.dp),
                value = score,
                stepSize = StepSize.HALF,
                style = RatingBarStyle.Default,
                onValueChange = {
                    score = it
                },
                onRatingChanged = {
                    Log.i("TAG", "SelfSatisfaction Rating Changed: $it | $score")
                    onValueChanged((it * 2).toInt())
                }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}


@Composable
internal fun MinimalHelpText(text: String, modifier: Modifier = Modifier) {
    LockedDirection(LayoutDirection.Ltr) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = text, style = MaterialTheme.typography.labelMedium.copy(
                    textAlign = TextAlign.Justify,
                    textDirection = TextDirection.ContentOrRtl,
                    color = MaterialTheme.colorScheme.outline
                ), modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "A Help Text",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AiAdvice(
    reportType: BaseApplication.ReportType,
    uiState: MainUiState,
    modifier: Modifier = Modifier,
    test: Boolean = false
) {
    val advice by remember { uiState.advice }
    val hasAdvice = when (reportType) {
        BaseApplication.ReportType.Daily ->
            (uiState.dailyReports?.list?.size ?: 0) >= 2

        BaseApplication.ReportType.Weekly ->
            (uiState.weeklyReports?.list?.size ?: 0) >= 2
    }
    var expanded by remember { mutableStateOf(false) }
    if (hasAdvice || test) {
        LockedDirection(LayoutDirection.Ltr) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                shape = MaterialTheme.shapes.large.copy(
                    CornerSize(25.dp)
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        val (text, icon) = createRefs()
                        Text(
                            text = advice.let {
                                if ((it
                                        ?: "").length >= MAX_OF_DISPLAYED_CHAR_COLLAPSE && !expanded
                                ) "${it?.substring(0..MAX_OF_DISPLAYED_CHAR_COLLAPSE)} ..." else it
                            } ?: "در حال تحلیل و بررسی گزارش ...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDirection = TextDirection.ContentOrRtl,
                                textAlign = TextAlign.Justify
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(vertical = 13.dp, horizontal = 10.dp)
                                .verticalScroll(rememberScrollState())
                                .constrainAs(text) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(icon.start)
                                },
                        )
                        Box(modifier = Modifier
                            .padding(top = 18.dp, end = 10.dp, bottom = 10.dp)
                            .constrainAs(icon) {
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(19.dp),
                                painter = painterResource(R.drawable.ai_advice),
                                contentDescription = "Ai Advice",
                            )
                        }
                    }
                    if (!advice.isNullOrEmpty()) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(-3.dp, -3.dp),
                            onClick = {
                                expanded = !expanded
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = if (expanded) painterResource(R.drawable.rounded_collapse_24) else painterResource(
                                    R.drawable.rounded_expand_24
                                ),
                                contentDescription = "Expand"
                            )
                        }
                    }
                }
            }
        }
    }
}
