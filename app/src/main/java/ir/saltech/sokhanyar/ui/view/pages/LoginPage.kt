package ir.saltech.sokhanyar.ui.view.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.sokhanyar.BaseApplication.LoginScreens
import ir.saltech.sokhanyar.R
import ir.saltech.sokhanyar.model.data.general.OtpRequestStatus
import ir.saltech.sokhanyar.ui.view.components.LockedDirection
import ir.saltech.sokhanyar.ui.view.model.MainViewModel
import ir.saltech.sokhanyar.util.epochToMinutesSeconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(
	androidx.compose.material3.ExperimentalMaterial3Api::class,
	androidx.compose.animation.ExperimentalAnimationApi::class
)
@Composable
fun LoginPage(modifier: Modifier = Modifier, mainViewModel: MainViewModel = viewModel(), onCompleted: () -> Unit) {
	val density = LocalDensity.current
	val uiState by mainViewModel.uiState.collectAsState()
	val isOtpCodeRequested by remember {
		derivedStateOf { uiState.user.authInfo?.otpRequestStatus }
	}
	var loading by rememberSaveable { mutableStateOf(false) }
	var savedPhoneNumber by rememberSaveable { mutableLongStateOf(0) }
	var currentScreen by remember { mutableStateOf(LoginScreens.Login) }

	val startLogin = { phoneNumber: Long ->
		loading = true
		mainViewModel.viewModelScope.launch(Dispatchers.IO) {
			mainViewModel.doLogin(phoneNumber = phoneNumber)
		}
	}

	val handleOtpVerify = { otpCode: Int ->
		loading = true
		mainViewModel.viewModelScope.launch(Dispatchers.IO) {
			mainViewModel.doVerifyOtp(phoneNumber = savedPhoneNumber, otpCode = otpCode, onCompleted)
		}
	}

	LockedDirection(direction = LayoutDirection.Rtl) {
		Surface(
			modifier = modifier
				.fillMaxSize()
				.padding(bottom = with(density) {
					WindowInsets.ime.getBottom(density).toDp()
				}), color = MaterialTheme.colorScheme.background
		) {
			LaunchedEffect(isOtpCodeRequested) {
				loading = false
				currentScreen = when (isOtpCodeRequested) {
					OtpRequestStatus.REQUESTED -> {
						mainViewModel.startCountdown()
						LoginScreens.Otp
					}
					else -> {
						mainViewModel.resetOtpRequestStatus()
						LoginScreens.Login
					}
				}
			}

			AnimatedContent(
				targetState = currentScreen, transitionSpec = {
					if (targetState == LoginScreens.Otp) {
						slideInHorizontally { -it / 2 } + fadeIn() togetherWith slideOutHorizontally { it / 2 } + fadeOut()
					} else {
						slideInHorizontally { it / 2 } + fadeIn() togetherWith slideOutHorizontally { -it / 2 } + fadeOut()
					}
				}) { screen ->
				when (screen) {
					LoginScreens.Login -> LoginPageContent(
						loading = loading, onLoginClick = { phoneNumber ->
							savedPhoneNumber = phoneNumber
							startLogin(phoneNumber)
						})

					LoginScreens.Otp -> OtpPageContent(
						loading = loading,
						onOtpVerify = { otpCode -> handleOtpVerify(otpCode)  },
						onResendOtp = {
							mainViewModel.resetOtpRequestStatus()
							startLogin(savedPhoneNumber)
						})
				}
			}
		}
	}
}


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LoginPageContent(loading: Boolean = false, mainViewModel: MainViewModel = viewModel(), onLoginClick: (Long) -> Unit) {
	var phoneNumber: Long? by rememberSaveable { mutableStateOf(null) }
	val errorMessage: String by mainViewModel.errorMessage.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(id = (if (isSystemInDarkTheme()) R.drawable.sokhanyar_dark else R.drawable.sokhanyar_light)),
			contentDescription = "App Logo",
			modifier = Modifier.width(150.dp)
		)
		Spacer(modifier = Modifier.height(40.dp))
		Text(
			text = "برای ورود یا عضویت، شماره موبایل خود را وارد کنید.",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedTextField(
			value = (phoneNumber
				?: "").toString(), // Replace with your state variable
			onValueChange = { phoneNumber = if (it.length < 11) it.toLongOrNull() else null; mainViewModel.resetErrorMessage() },
			isError = errorMessage.isNotEmpty(),
			supportingText = {
				if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("شماره موبایل بدون صفر باشد. مثلاً 9101122334")
                }
			},
			label = { Text("شماره موبایل") },
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(25.dp),
			singleLine = true,
			enabled = !loading,
			textStyle = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Go
			),
		)
		Spacer(modifier = Modifier.height(24.dp))
		Button(
			onClick = {
				onLoginClick(phoneNumber ?: return@Button)
			}, // Navigate to OTP screen
			enabled = !loading && errorMessage.isEmpty(), modifier = Modifier.fillMaxWidth()
		) {
			Text(if (loading) "در حال انجام ..." else "ورود / عضویت")
		}
	}
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OtpPageContent(
    loading: Boolean = false,
    mainViewModel: MainViewModel = viewModel(),
    onOtpVerify: (Int) -> Unit,
    onResendOtp: () -> Unit
) {
	val focus = LocalFocusManager.current
	val remainingTime: Long by mainViewModel.remainingTime.collectAsState()
	val errorMessage: String by mainViewModel.errorMessage.collectAsState()
	var otp: Int? by rememberSaveable { mutableStateOf(null) }
	
	LaunchedEffect(Unit) {
		focus.moveFocus(FocusDirection.Down)
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(id = (if (isSystemInDarkTheme()) R.drawable.sokhanyar_dark else R.drawable.sokhanyar_light)),
			contentDescription = "App Logo",
			modifier = Modifier.width(150.dp)
		)
		Spacer(modifier = Modifier.height(40.dp))
		Text(
			text = "کد تایید ارسال شده به شماره موبایل خود را وارد کنید.",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedTextField(
			value = (otp ?: "").toString(), // Replace with your OTP state variable
			onValueChange = { otp = if (it.length <= 6) it.toIntOrNull() else null },
			label = { Text("کد فعالسازی") },
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(25.dp),
			singleLine = true,
			enabled = !loading,
			suffix = {
				if (remainingTime > 0) {
					Text(modifier = Modifier.padding(start = 8.dp, end = 5.dp), text = remainingTime.epochToMinutesSeconds(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
				} else {
					TextButton(onClick = {
						onResendOtp()
					}, enabled = !loading) {
						Text(text = "ارسال مجدد", style = MaterialTheme.typography.labelLarge)
					}
				}
			},
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Go
			),
			isError = errorMessage.isNotEmpty(),
			textStyle = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr),
			supportingText = {
				if (errorMessage.isNotEmpty()) {
					Text(errorMessage, color = MaterialTheme.colorScheme.error)
				}
			}
		)
		Spacer(modifier = Modifier.height(24.dp))
		Button(
			onClick = { onOtpVerify(otp ?: return@Button) },
			enabled = !loading && errorMessage.isEmpty(), modifier = Modifier.fillMaxWidth()
		) {
			Text(if (loading) "در حال انجام ..." else "ورود به برنامه")
		}
	}
}

@Preview(
	showBackground = true,
	uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun LoginPagePreview() {
	LoginPage {}
}