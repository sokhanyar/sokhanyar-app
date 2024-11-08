package ir.saltech.myapps.stutter.ui.view.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.dto.model.ui.MenuPageItem
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection
import ir.saltech.myapps.stutter.ui.view.model.MainViewModel
import ir.saltech.myapps.stutter.util.wrapToScreen

@Composable
fun MainPage(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    motivationText: String,
    menuPageItems: List<MenuPageItem>,
    mainViewModel: MainViewModel = viewModel(),
    onPageWanted: (BaseApplication.Page) -> Unit
) {
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
//                    IconButton(
//                        modifier = Modifier.size(48.dp),
//                        onClick = {
//                            onPageWanted(BaseApplication.Page.Search)
//                        }
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_search_24),
//                            contentDescription = "search",
//                        )
//                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
//                    IconButton(
//                        modifier = Modifier.size(48.dp),
//                        onClick = {
//                            onPageWanted(BaseApplication.Page.Menu)
//                        }
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_menu_24),
//                            contentDescription = "menu",
//                        )
//                    }
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
                    colors = ButtonDefaults.filledTonalButtonColors(
                        Color(if (isSystemInDarkTheme()) 0xFFB1C0DA else 0xBE286BDA).copy(
                            alpha = 0.08f
                        ),
                        ButtonDefaults.filledTonalButtonColors().contentColor,
                        ButtonDefaults.filledTonalButtonColors().disabledContainerColor,
                        ButtonDefaults.filledTonalButtonColors().disabledContentColor,
                    ),
                    onClick = {
                        onPageWanted(BaseApplication.Page.ChatRoom)
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
            LockedDirection(LayoutDirection.Rtl) {
                LazyVerticalStaggeredGrid(
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
//                .background(
//                    ShaderBrush(ImageShader(ImageBitmap.imageResource(R.drawable.white_pattern), TileMode.Clamp, TileMode.Repeated))
//                )
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
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
    }
}

@Composable
fun MenuItemButton(menuPageItem: MenuPageItem) {
    val enabled = menuPageItem.disabledReason == null
    var showReason by remember { mutableStateOf(false) }
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
                    MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.7f),
                    RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.blur(
                    if (menuPageItem.comingSoon) 32.dp else 0.dp,
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
            if (!enabled && !menuPageItem.disabledReason.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp)
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.TopStart
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp),
                        onClick = {
                            showReason = true
                        }) {
                        Icon(
                            Icons.Outlined.Info,
                            tint = MaterialTheme.colorScheme.contentColorFor(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else ButtonDefaults.filledTonalButtonColors().disabledContainerColor)
                                .copy(alpha = 0.58f),
                            contentDescription = "Why This is Disabled?"
                        )
                    }
                }
            }
            if (menuPageItem.comingSoon) {
                Box(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceContainerLowest.copy(
                            alpha = 0.5f
                        ), shape = MaterialTheme.shapes.small
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "به زودی ...",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.ContentOrRtl),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Sample preview - This can be removed when integrating into the app.
@Composable
@Preview(showBackground = true)
fun PreviewStutterAidWelcomeScreen() {
    MainPage(motivationText = "امروز، تو لکنت رو شکست میدی! 🦾", menuPageItems = emptyList()) {

    }
}
