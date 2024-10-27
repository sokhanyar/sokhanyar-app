package ir.saltech.myapps.stutter.ui.view.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize

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
        style = MaterialTheme.typography.headlineSmall.copy(textDirection = TextDirection.ContentOrRtl),
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
fun TextFieldLayout(title: String, valueRange: IntRange, value: Int?, onValueChanged: (Int?) -> Unit, last: Boolean = false, enabled: Boolean = true, suffix: (@Composable () -> Unit)? = null, supportText: (@Composable () -> Unit)? = null) {
    val focus = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = if (last) ImeAction.Done else ImeAction.Next),
        keyboardActions = if (last) KeyboardActions(onDone = { focus.clearFocus() }) else KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SelfSatisfactionLayout(value: Int, onValueChanged: (Int) -> Unit) {
    Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "رضـایـت از خـودم", modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 21.sp, fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        LockedDirection {
            var score by remember { mutableFloatStateOf(value.toFloat()) }
            RatingBar(modifier = Modifier.padding(horizontal = 16.dp), value = score, stepSize = StepSize.HALF,
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
fun LoginScreen(onLoginClick: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    println("Rendering LoginScreen...")
    println("Current email: \$email")
    println("Current password: \$password")
    println("Is error state: \$isError")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isError = false
                println("Email updated: \$email")
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isError = false
                println("Password updated: \$password")
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                println("Login button clicked")
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    println("Login credentials are valid. Proceeding with login...")
                    onLoginClick(email, password)
                } else {
                    isError = true
                    println("Login credentials are invalid. Error state set to true.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        if (isError) {
            println("Displaying error message: Please fill in all fields.")
            Text(
                text = "Please fill in all fields.",
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    println("Previewing LoginScreen...")
    LoginScreen { _, _ -> }
}

