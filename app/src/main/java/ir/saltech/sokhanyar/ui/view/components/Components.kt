package ir.saltech.sokhanyar.ui.view.components

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
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
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.Constants.MAX_OF_DISPLAYED_CHAR_COLLAPSE
import ir.saltech.sokhanyar.R
import ir.saltech.sokhanyar.model.ui.MenuPageItem
import ir.saltech.sokhanyar.ui.state.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LockedDirection(
	direction: LayoutDirection = LayoutDirection.Ltr, content: @Composable () -> Unit,
) {
	CompositionLocalProvider(LocalLayoutDirection provides direction) {
		content()
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TreatMethodUsageObject(title: String, value: Int?, onValueChanged: (Int?) -> Unit) {
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
	modifier: Modifier = Modifier,
	selectedChoice: String? = null,
	supportText: @Composable (() -> Unit)? = null,
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
	modifier: Modifier = Modifier,
	last: Boolean = false,
	enabled: Boolean = true,
	suffix: (@Composable () -> Unit)? = null,
	supportText: (@Composable () -> Unit)? = null,
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
fun StutterSeverityRatingLayout(value: Int, onValueChanged: (Int) -> Unit) {
	var ssrValue by remember {
		mutableFloatStateOf(value.toFloat())
	}
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = "درجه شدت لکنت",
			modifier = Modifier
				.padding(horizontal = 16.dp),
			style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
			textAlign = TextAlign.Center
		)
		Spacer(modifier = Modifier.height(8.dp))
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceAround,
			verticalAlignment = Alignment.CenterVertically
		) {
			Slider(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
				value = ssrValue,
				onValueChange = { ssrValue = it },
				valueRange = 0f..9f,
				steps = 8,
				onValueChangeFinished = {
					onValueChanged(ssrValue.roundToInt())
				}
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				modifier = Modifier
					.padding(start = 8.dp)
					.background(
						color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
						shape = RoundedCornerShape(100.dp)
					),
				text = "   ${ssrValue.roundToInt()}   ",
				style = MaterialTheme.typography.bodySmall.copy(
					textDirection = TextDirection.Rtl,
					fontSize = 16.sp
				),
				textAlign = TextAlign.Center
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			modifier = Modifier.fillMaxWidth(),
			text = "به طور میانگین، امروز در موقعیت های مختلف، نسبت تعداد کلماتی که لکنت کردین به کل جملاتی که گفتین، چقدر بوده.",
			style = MaterialTheme.typography.labelMedium.copy(textDirection = TextDirection.Rtl),
			color = MaterialTheme.colorScheme.secondary
		)
	}
	Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelfSatisfactionLayout(value: Int, onValueChanged: (Int) -> Unit) {
	Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = "رضـایـت از خـودم",
			modifier = Modifier
				.padding(horizontal = 16.dp),
			style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
			textAlign = TextAlign.Center
		)
		Spacer(modifier = Modifier.height(8.dp))
		LockedDirection {
			var score by remember { mutableFloatStateOf(value.toFloat()) }
			RatingBar(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.scale(0.8f),
				value = score,
				stepSize = StepSize.HALF,
				style = RatingBarStyle.Default,
				onValueChange = {
					score = it
				},
				onRatingChanged = {
					onValueChanged((it * 2).toInt())
				}
			)
		}
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp),
			text = "یعنی چقدر امروز، از تمرین و اجرای شیوه خود در محیط های مختلف راضی هستین.",
			style = MaterialTheme.typography.labelMedium.copy(textDirection = TextDirection.Rtl),
			color = MaterialTheme.colorScheme.secondary
		)
		Spacer(modifier = Modifier.height(16.dp))
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
) {
	val advice by remember { uiState.realAdvice }
	val hasAdvice = when (reportType) {
		BaseApplication.ReportType.Daily ->
			(uiState.dailyReports?.list?.size ?: 0) >= 2

		BaseApplication.ReportType.Weekly ->
			(uiState.weeklyReports?.list?.size ?: 0) >= 2
	}
	var expanded by remember { mutableStateOf(false) }
	if (hasAdvice) {
		LockedDirection(LayoutDirection.Ltr) {
			Box(
				modifier = modifier
					.fillMaxWidth()
					.padding(16.dp)
					.let {
						if (advice.isNullOrEmpty()) {
							it.shimmer(
								rememberShimmer(
									shimmerBounds = ShimmerBounds.View,
									theme = LocalShimmerTheme.current.copy(rotation = 180f)
								)
							)
						} else {
							it
						}
					}
					.background(
						Brush.horizontalGradient(listOf(Color(0x295758BB), Color(0x29A759C5))),
						shape = MaterialTheme.shapes.large.copy(
							CornerSize(25.dp)
						)
					)
					.border(
						1.5.dp,
						Brush.horizontalGradient(listOf(Color(0xFF5758BB), Color(0xFFA759C5))),
						shape = MaterialTheme.shapes.large.copy(
							CornerSize(25.dp)
						)
					)
			) {
				Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
					AnimatedContent(advice) { adviceText ->
						ConstraintLayout(
							modifier = Modifier
								.fillMaxWidth()
								.padding(10.dp)
						) {
							val (text, icon) = createRefs()
							if (adviceText.isNullOrEmpty()) {
								Text(
									text = "در حال تحلیل گزارش شما ...",
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
							} else {
								SelectionContainer() {
									Text(
										text = adviceText.let {
											if (it.length >= MAX_OF_DISPLAYED_CHAR_COLLAPSE && !expanded
											) "${it.substring(0..MAX_OF_DISPLAYED_CHAR_COLLAPSE)} ..." else it
										},
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
								}
							}
							Box(
								modifier = Modifier
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
						if (!adviceText.isNullOrEmpty() && adviceText.length > MAX_OF_DISPLAYED_CHAR_COLLAPSE) {
							IconButton(
								modifier = Modifier
									.align(Alignment.TopStart)
									.padding(16.dp)
									.offset((-3).dp, (-3).dp),
								onClick = {
									expanded = !expanded
								}
							) {
								Icon(
									modifier = Modifier
										.size(18.dp)
										.paint(
											BrushPainter(
												Brush.horizontalGradient(
													listOf(
														Color(0xFF5758BB), Color(0xFFA759C5)
													)
												)
											)
										),
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
}

@Composable
fun MenuItemButton(menuPageItem: MenuPageItem) {
	val scope = rememberCoroutineScope()
	val enabled = menuPageItem.disabledReason == null
	var showReason by rememberSaveable { mutableStateOf(false) }
	if (showReason) {
		LockedDirection(LayoutDirection.Rtl) {
			AlertDialog(
				onDismissRequest = { showReason = false },
				confirmButton = {
					Button(onClick = { showReason = false }) { Text("متوجه شدم") }
				},
				title = { Text(menuPageItem.title) },
				text = { Text("${menuPageItem.disabledReason}") })
		}
	}
	LockedDirection(LayoutDirection.Ltr) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp)
				.border(
					0.8.dp,
					MaterialTheme.colorScheme.let {
						if (isSystemInDarkTheme()) it.surfaceBright else it.surfaceDim
					}.copy(alpha = 0.7f),
					RoundedCornerShape(15.dp)
				),
			contentAlignment = Alignment.Center
		) {
			Card(
				modifier = Modifier.blur(
					if (menuPageItem.comingSoon) 21.dp else 0.dp,
					edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(15.dp))
				),
				colors = CardDefaults.cardColors(
					ButtonDefaults.filledTonalButtonColors().containerColor.copy(alpha = 0.58f),
					ButtonDefaults.filledTonalButtonColors().contentColor,
					ButtonDefaults.filledTonalButtonColors().disabledContainerColor,
					ButtonDefaults.filledTonalButtonColors().disabledContentColor,
				),
				shape = RoundedCornerShape(15.dp),
				enabled = enabled && !menuPageItem.comingSoon,
				onClick = {
					menuPageItem.onClick()
				}
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(10.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.background(
								MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.48f),
								RoundedCornerShape(9.dp)
							)
					) {
						Image(
							painter = painterResource(id = menuPageItem.iconResId),
							contentDescription = null,
							modifier = Modifier
								.fillMaxWidth()
								.padding(13.dp)
						)
						if (!enabled) {
							Spacer(
								modifier = Modifier
									.matchParentSize()
									.background(
										MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.78f),
										RoundedCornerShape(9.dp)
									)
							)
						}
					}
					Spacer(modifier = Modifier.height(8.dp))
					Text(
						modifier = Modifier.padding(horizontal = 3.dp),
						text = menuPageItem.title,
						style = MaterialTheme.typography.bodyLarge.copy(
							fontSize = 17.sp,
							textDirection = TextDirection.ContentOrRtl
						),
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.height(5.dp))
				}
			}
			if (!enabled && !menuPageItem.disabledReason.isEmpty()) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(13.dp)
						.align(Alignment.TopEnd),
					contentAlignment = Alignment.TopEnd
				) {
					Row(
						modifier = Modifier
							.border(
								0.5.dp,
								MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
								shape = RoundedCornerShape(100.dp)
							)
							.background(
								color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
								shape = RoundedCornerShape(100.dp)
							)
							.clip(RoundedCornerShape(100.dp))
							.clickable {
								showReason = true
							}) {
						var isShowHint by rememberSaveable {
							mutableStateOf(false)
						}
						LaunchedEffect(Unit) {
							scope.launch(Dispatchers.IO) {
								delay(2743)
								isShowHint = true
								delay(8432)
								isShowHint = false
								delay(100)
							}
						}
						AnimatedContent(isShowHint) { showed ->
							Row(modifier = Modifier.padding(8.dp)) {
								val disabledColorTint =
									MaterialTheme.colorScheme.contentColorFor(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else ButtonDefaults.filledTonalButtonColors().disabledContainerColor)
										.copy(alpha = 0.58f)
								if (showed) {
									Text(
										modifier = Modifier.padding(horizontal = 2.dp),
										text = "چرا خاموشه؟",
										style = MaterialTheme.typography.labelSmall.copy(
											textDirection = TextDirection.Rtl,
											fontWeight = FontWeight.Bold
										),
										textAlign = TextAlign.Center,
										color = disabledColorTint
									)
									Spacer(modifier = Modifier.width(3.dp))
								}
								Icon(
									Icons.Outlined.Info,
									modifier = Modifier.size(18.dp),
									tint = disabledColorTint,
									contentDescription = "Why This is Disabled?"
								)
							}
						}
					}
				}
			}
			if (menuPageItem.comingSoon) {
				Box(
					modifier = Modifier
						.background(
							MaterialTheme.colorScheme.surfaceContainerLowest.copy(
								alpha = 0.28f
							), shape = MaterialTheme.shapes.small.copy(all = CornerSize(25.dp))
						)
						.border(
							width = 0.35.dp, color = MaterialTheme.colorScheme.surface.copy(
								alpha = 0.1f
							), shape = MaterialTheme.shapes.small.copy(all = CornerSize(25.dp))
						)
				) {
					Text(
						modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
						text = "به زودی ...",
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.73f),
						style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.ContentOrRtl),
						textAlign = TextAlign.Center
					)
				}
			}
		}
	}
}
