package ir.saltech.myapps.stutter.ui.view.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.R
import ir.saltech.myapps.stutter.ui.view.components.LockedDirection

@Composable
fun MainPage(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    motivationText: String,
    onPageWanted: (BaseApplication.Page) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .fillMaxSize()
//            .background(
//                ShaderBrush(ImageShader(ImageBitmap.imageResource(R.drawable.white_pattern), TileMode.Clamp, TileMode.Repeated))
//            )
    ) {
        // Header Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .let {
                    if (LocalConfiguration.current.screenHeightDp.dp < 600.dp) {
                        it.fillMaxSize()
                    } else {
                        it.weight(0.8f)
                    }
                },
            shape = MaterialTheme.shapes.large.copy(all = CornerSize(0))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.background_dark else R.drawable.background_light),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f)),
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    ) {
                        val (header, motivation, button, spacer) = createRefs()
                        Row(
                            modifier = Modifier
                                .padding(top = 21.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .fillMaxWidth()
                                .constrainAs(header) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                    start.linkTo(parent.start)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = {
                                    onPageWanted(BaseApplication.Page.Search)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_search_24),
                                    contentDescription = "search",
                                )
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
                                    onPageWanted(BaseApplication.Page.Menu)
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
                            LockedDirection(LayoutDirection.Ltr) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp, horizontal = 24.dp),
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 21.sp,
                                        lineHeight = 40.sp,
                                        textDirection = TextDirection.Rtl
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        FilledTonalButton(
                            modifier = Modifier
                                .padding(top = 8.dp, end = 16.dp, start = 16.dp, bottom = 24.dp)
                                .constrainAs(button) {
                                    top.linkTo(motivation.bottom)
                                    bottom.linkTo(spacer.bottom)
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
                        Spacer(modifier = Modifier
                            .height(32.dp)
                            .constrainAs(spacer) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                                start.linkTo(parent.start)
                            })
                    }
                }
            }
        }
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .offset(y = (-18).dp)
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLowest,
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
                .padding(horizontal = 16.dp),
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
            item {
                MenuItem(
                    iconResId = R.drawable.analysis,
                    title = "تحلیل تمرین",
                    onClick = {
                        onPageWanted(BaseApplication.Page.AnalyzePractice)
                    })
            }
            item {
                MenuItem(
                    iconResId = R.drawable.podcast,
                    title = "تمرین صوتی",
                    onClick = {
                        onPageWanted(BaseApplication.Page.Practice)
                    })
            }
            item {
                MenuItem(
                    iconResId = R.drawable.schedule,
                    title = "گزارش هفتگی",
                    onClick = {
                        onPageWanted(BaseApplication.Page.SendWeeklyReport)
                    })
            }
            item {
                MenuItem(
                    iconResId = R.drawable.planing,
                    title = "گزارش روزانه",
                    onClick = {
                        onPageWanted(BaseApplication.Page.SendDailyReport)
                    })
            }
        }
        // Menu Items Section
    }
}

@Composable
fun MenuItem(iconResId: Int, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            ButtonDefaults.filledTonalButtonColors().containerColor.copy(alpha = 0.58f),
            ButtonDefaults.filledTonalButtonColors().contentColor,
            ButtonDefaults.filledTonalButtonColors().disabledContainerColor,
            ButtonDefaults.filledTonalButtonColors().disabledContentColor,
        ),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            onClick()
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
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.48f),
                        RoundedCornerShape(9.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(13.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(horizontal = 3.dp),
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    textDirection = TextDirection.ContentOrRtl
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

// Sample preview - This can be removed when integrating into the app.
@Composable
@Preview(showBackground = true)
fun PreviewStutterAidWelcomeScreen() {
    MainPage(motivationText = "امروز، تو لکنت رو شکست میدی! 🦾") {

    }
}
