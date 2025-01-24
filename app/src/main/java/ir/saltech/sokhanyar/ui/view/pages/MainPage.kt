package ir.saltech.sokhanyar.ui.view.pages

import android.content.Intent
import android.provider.Browser
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.Constants.MAX_DONATION_PRICE_IRR
import ir.saltech.sokhanyar.BaseApplication.Constants.MIN_DONATION_PRICE_IRR
import ir.saltech.sokhanyar.R
import ir.saltech.sokhanyar.model.ui.MenuPageItem
import ir.saltech.sokhanyar.ui.view.components.LockedDirection
import ir.saltech.sokhanyar.ui.view.components.MenuItemButton
import ir.saltech.sokhanyar.ui.view.model.MainViewModel
import ir.saltech.sokhanyar.util.checkScreenIsMinimal
import ir.saltech.sokhanyar.util.showingPrice
import ir.saltech.sokhanyar.util.toPrice
import ir.saltech.sokhanyar.util.wrapToScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
	innerPadding: PaddingValues = PaddingValues(0.dp),
	motivationText: String,
	menuPageItems: List<MenuPageItem>,
	mainViewModel: MainViewModel = viewModel(),
	onPageWanted: (BaseApplication.Page) -> Unit
) {
	val isScreenMinimal = checkScreenIsMinimal()
	val sheetState = rememberModalBottomSheetState()
	var isShowedMenuItems: Boolean by rememberSaveable {
		mutableStateOf(false)
	}
	var isShowedDonationDialog: Boolean by remember {
		mutableStateOf(false)
	}
	if (isShowedMenuItems) {
		ModalBottomSheet(
			modifier = Modifier.fillMaxSize().padding(innerPadding),
			onDismissRequest = {
				isShowedMenuItems = false
			},
			sheetState = sheetState
		) {
			if (isScreenMinimal) {
				Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				) {
					// TODO: مشکل دکمه BACK و کلاً بازگشتش رو درست کن.
					AppMenuItems(
						menuPageItems,
						modifier = Modifier
							.weight(1f)
							.background(
								MaterialTheme.colorScheme.surface,
								MaterialTheme.shapes.large.copy(
									topEnd = CornerSize(16.dp),
									topStart = CornerSize(16.dp),
									bottomEnd = CornerSize(0),
									bottomStart = CornerSize(0)
								)
							)
							.padding(horizontal = 16.dp)
							.padding(bottom = innerPadding.calculateBottomPadding()),
					)
				}
			} else {
				Column (modifier = Modifier.fillMaxHeight(0.5f).padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
					Text(modifier = Modifier.fillMaxWidth(), text = "آیتم ها به زودی اضافه می شوند.. ", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Rtl))
				}
			}
		}
	}
	if(isShowedDonationDialog) {
		DonationDialog {
			isShowedDonationDialog = false
		}
	}
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(
				ShaderBrush(
					ImageShader(
						ImageBitmap.imageResource(if (isSystemInDarkTheme()) R.drawable.sokhanyar_background_dark else R.drawable.sokhanyar_background_light),
						TileMode.Decal,
						TileMode.Mirror
					)
				)
			)

	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
		) {
			ConstraintLayout(
				modifier = Modifier
					.fillMaxWidth()
					.wrapToScreen()
					.padding(innerPadding),
			) {
				val (header, motivation, button) = createRefs()
				Row(
					modifier = Modifier
						.padding(top = 21.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
						.fillMaxWidth()
						.constrainAs(header) {
							top.linkTo(parent.top)
							end.linkTo(parent.end)
							start.linkTo(parent.start)
//                                    bottom.linkTo(motivation.top)
						},
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					// TODO: Complete the menu, settings and search sections.
					Box {
						IconButton(
							modifier = Modifier.size(48.dp),
							onClick = {
								isShowedDonationDialog = true
							}
						) {
							Icon(
								painter = painterResource(id = R.drawable.donate),
								contentDescription = "donate",
							)
						}
//							IconButton(
//								modifier = Modifier.size(48.dp),
//								onClick = {
//									onPageWanted(BaseApplication.Page.Search)
//								}
//							) {
//								Icon(
//									painter = painterResource(id = R.drawable.baseline_search_24),
//									contentDescription = "search",
//								)
//							}
					}
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						modifier = Modifier.padding(5.dp),
						text = stringResource(R.string.app_name),
						style = MaterialTheme.typography.headlineSmall
					)
					Spacer(modifier = Modifier.width(8.dp))
					IconButton(
						modifier = Modifier.size(48.dp),
						onClick = {
//								// TODO: برای صفحه های کوچیک، اون لیست منوی پایین، به صورت لیست سطری نشون داده بشه و زیرش هم با فاصله زیاد، آیتم های داخل منوی اصلی نشون داده بشن . مث تنظیمات و اینا
//								if (isScreenMinimal) {
//									isShowedPageMenuItems = true
//								} else {
//									onPageWanted(BaseApplication.Page.Menu)
//								}
							isShowedMenuItems = true
						}
					) {
						Icon(
							painter = painterResource(id = R.drawable.baseline_menu_24),
							contentDescription = "menu",
						)
					}
				}
				AnimatedContent(modifier = Modifier.constrainAs(motivation) {
					top.linkTo(header.bottom)
					end.linkTo(parent.end)
					start.linkTo(parent.start)
					bottom.linkTo(button.top)
				}, targetState = motivationText) { text ->
					Text(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 32.dp, horizontal = 24.dp)
							.clip(RoundedCornerShape(25.dp))
							.clickable {
								mainViewModel.generateNewMotivationText()
							},
						text = text,
						style = MaterialTheme.typography.bodyLarge.copy(
							fontSize = 21.sp,
							lineHeight = 40.sp,
							textDirection = TextDirection.Rtl
						),
						textAlign = TextAlign.Center
					)
				}
				FilledTonalButton(
					modifier = Modifier
						.padding(bottom = 32.dp, end = 16.dp, start = 16.dp)
						.constrainAs(button) {
//                                    top.linkTo(motivation.bottom)
							bottom.linkTo(parent.bottom)
							end.linkTo(parent.end)
							start.linkTo(parent.start)
						},
					border = BorderStroke(
						0.35.dp,
						color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
					),
					colors = ButtonDefaults.filledTonalButtonColors(
						Color(if (isSystemInDarkTheme()) 0xFFB1C0DA else 0xBE286BDA).copy(
							alpha = 0.09f
						),
						ButtonDefaults.filledTonalButtonColors().contentColor,
						ButtonDefaults.filledTonalButtonColors().disabledContainerColor,
						ButtonDefaults.filledTonalButtonColors().disabledContentColor,
					),
					onClick = {
						onPageWanted(BaseApplication.Page.AiChatRoom)
					}
				) {
					Text(
						text = "باهام حرف بزن!",
						style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl),
						textAlign = TextAlign.Center,
					)
					Spacer(modifier = Modifier.width(8.dp))
					Icon(
						painter = painterResource(R.drawable.chatbot1),
						contentDescription = "Chat with AI"
					)
				}
			}
			AppMenuItems(
				menuPageItems,
				modifier = Modifier
					.weight(1f)
					.background(
						MaterialTheme.colorScheme.surface,
						MaterialTheme.shapes.large.copy(
							topEnd = CornerSize(16.dp),
							topStart = CornerSize(16.dp),
							bottomEnd = CornerSize(0),
							bottomStart = CornerSize(0)
						)
					)
					.padding(top = 24.dp)
					.padding(horizontal = 16.dp)
					.padding(bottom = innerPadding.calculateBottomPadding()),
			)
		}
	}
}

@Composable
fun AppMenuItems(menuPageItems: List<MenuPageItem>, modifier: Modifier = Modifier) {
	LockedDirection(LayoutDirection.Rtl) {
		LazyVerticalStaggeredGrid(
			modifier = modifier,
			columns = StaggeredGridCells.Adaptive(
				if (LocalConfiguration.current.screenHeightDp.dp < 600.dp) {
					75.dp
				} else {
					125.dp
				}
			),
			verticalItemSpacing = 0.dp,
			horizontalArrangement = Arrangement.SpaceAround
		) {
			items(menuPageItems) {
				MenuItemButton(it)
			}
		}
	}
}

@Composable
fun DonationDialog(
	modifier: Modifier = Modifier,
	mainViewModel: MainViewModel = viewModel(),
	onDismissRequest: () -> Unit
) {
	val uiState by mainViewModel.uiState.collectAsState()
	val loadedUserPhoneNumber = uiState.user.authInfo?.phoneNumber
	var wantedDonationPrice: Long? by remember {
		mutableStateOf(null)
	}
	var wantedDonationPhoneNumber: Long? by remember {
		mutableStateOf(loadedUserPhoneNumber)
	}
	var paymentLoading by remember {
		mutableStateOf(false)
	}
	LockedDirection(LayoutDirection.Rtl) {
		AlertDialog(
			modifier = modifier,
			onDismissRequest = {
				wantedDonationPrice = null
				wantedDonationPhoneNumber = null
				onDismissRequest()
			}, confirmButton = {
				Button(onClick = {
					if (wantedDonationPhoneNumber != null && wantedDonationPrice != null) {
						val submittedDonationPrice = wantedDonationPrice!! * 10_000
						if (submittedDonationPrice in MIN_DONATION_PRICE_IRR..MAX_DONATION_PRICE_IRR) {
							paymentLoading = true
							mainViewModel.doStartPayment(
								wantedDonationPhoneNumber!!,
								submittedDonationPrice
							) { trackId ->
								if (trackId != null) {
									val useBrowserToDoPayment = Intent(
										Intent.ACTION_VIEW,
										(BaseApplication.Constants.SALTECH_PAY_URL + "payment?trackId=${trackId}").toUri()
									)
									useBrowserToDoPayment.putExtra(
										Browser.EXTRA_APPLICATION_ID,
										mainViewModel.context.packageName
									)
									useBrowserToDoPayment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
									mainViewModel.context.startActivity(useBrowserToDoPayment)
									Toast.makeText(
										mainViewModel.context,
										"در ادامه، به درگاه پرداخت متصل میشوید.\nپیشاپیش، از حمایتتان متشکریم! 💝",
										Toast.LENGTH_SHORT
									).show()
									onDismissRequest()
								}
								paymentLoading = false
							}
						} else {
							Toast.makeText(
								mainViewModel.context,
								"مبلغ، باید حداقل ۵ هزار و حداکثر ۱۰ میلیون تومان باشد.",
								Toast.LENGTH_SHORT
							)
								.show()
						}
					} else {
						Toast.makeText(
							mainViewModel.context,
							"لطفاً شماره موبایل و مبلغ هدیه خود را وارد کنید.",
							Toast.LENGTH_SHORT
						).show()
					}
				}, enabled = !paymentLoading) {
					Text(if (paymentLoading) "درحال انجام" else "پرداخت")
				}
			}, dismissButton = {
				OutlinedButton(
					modifier = Modifier.padding(horizontal = 8.dp),
					enabled = !paymentLoading,
					onClick = {
						onDismissRequest()
					}) {
					Text("منصرف شدم")
				}
			}, title = {
				Text("حمایت از ما 🎁")
			}, text = {
				Column {
					Text("اگه از سخن یار خوشتون اومده و تونسته مشکلی رو ازتون حل کنه و اگه امکانش واسه تون هست، واسه پیشرفت سخن یار، ممنون میشیم حمایتمون کنین! 🙏🏻💐")
					Spacer(modifier = Modifier.height(24.dp))
					OutlinedTextField(
						value = wantedDonationPrice.showingPrice(),
						onValueChange = {
							if (it.length < 7) {
								wantedDonationPrice = it.toPrice()
							}
						},
						enabled = !paymentLoading,
						label = { Text("مبلغ هدیه") },
						placeholder = {
							Text(
								"مثلاً 10 = 10,000 تومان",
								style = MaterialTheme.typography.labelMedium
							)
						},
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						singleLine = true,
						suffix = {
							Text(
								"هزار تومان",
								style = MaterialTheme.typography.labelMedium
							)
						},
						supportingText = {
							Text(
								text = "مبلغ، بر پایه واحد هزار تومانه.",
								style = MaterialTheme.typography.labelSmall,
								color = MaterialTheme.colorScheme.secondary
							)
						})
					Spacer(modifier = Modifier.height(16.dp))
					OutlinedTextField(
						value = (wantedDonationPhoneNumber ?: "").toString(),
						onValueChange = {
							if (it.length < 11) {
								wantedDonationPhoneNumber = it.toLongOrNull()
							}
						},
						enabled = !paymentLoading && loadedUserPhoneNumber == null,
						label = { Text("شماره موبایل") },
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
						singleLine = true,
						supportingText = {
							Text(
								text = "واسه پیگیریهای بعدی، لطفاً شماره موبایل خودتون رو وارد کنین.",
								style = MaterialTheme.typography.labelSmall,
								color = MaterialTheme.colorScheme.secondary
							)
						})
					Spacer(modifier = Modifier.height(8.dp))
				}
			})
	}
}
