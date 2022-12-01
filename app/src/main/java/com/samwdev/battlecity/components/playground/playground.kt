package com.samwdev.battlecity.components.playground

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun Playground() {
    // MaterialTheme sets ContentAlpha.high as default
    Column {
        var topPadding by remember { mutableStateOf(10.dp) }
        val transition = updateTransition(targetState = topPadding, label = null)
        val topPaddingAnim by transition.animateDp(
            label = "",
            targetValueByState = { it },
        )
        AnimPlayground()
        Row(modifier = Modifier.paddingFromBaseline(topPaddingAnim)) {
            Button(onClick = { /*TODO*/ }) {

            }
            Text(text = "Sam Wei", modifier = Modifier.paddingFromBaseline(topPaddingAnim))
            Text(text = "Sam Wei")
            Button(onClick = { topPadding -= 50.dp }, modifier = Modifier.paddingFromBaseline(topPaddingAnim)) {
                Text(text = "UP")
            }
        }

        val width = remember { Animatable(200f) }
        BarChart(
            dataPoints = listOf(1,5,6,4,6,28,4,2),
            modifier = Modifier
                .background(Color.DarkGray)
                .height(200.dp)
                .width(width.value.dp)
                .paddingFromBaseline(topPaddingAnim)
        )

        val cor = rememberCoroutineScope()

        Button(
            onClick = {
//                    topPadding += 50.dp
                cor.launch { width.animateTo(400f) }
            },
            // Uses ButtonDefaults.ContentPadding by default
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp
            )
        ) {
            // Inner content including an icon and a text label
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favorite",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Like")
        }


    }

//        val scrollState2 = rememberLazyListState()
//        LazyColumn(
//            state = scrollState2,
//            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//            itemsIndexed((0..1000).map { it }) { index, item ->
//                Text(text = item.toString(), modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.Gray))
//            }
//        }

    var expanded by remember { mutableStateOf(false) }
    Column {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            onClick = { expanded = !expanded }
        ) {
            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150, 150)) with
                        fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (targetState) {
                                keyframes {
                                    // Expand horizontally first.
                                    IntSize(targetSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                            } else {
                                keyframes {
                                    // Shrink vertically first.
                                    IntSize(initialSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                }
                            }
                        }
                }
            ) { targetExpanded ->
                if (targetExpanded) {
                    Expanded()
                } else {
                    ContentIcon()
                }
            }
        }

        Box(modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .swipeToDismiss { }
            .background(Color.Blue)) {
            Text("This is a button")
        }

    }
}

@Composable
fun Expanded() {
    Column() {
        Text(modifier = Modifier.fillMaxWidth(), text = """
        I believe 그댄 곁에 없지만
        이대로 이별은 아니겠죠
        I believe 나에게 오는 길은
        조금 멀리 돌아올 뿐이겠죠
        모두 지나간 그 기억속에서
        내가 나를 아프게 하며 눈물을 만들죠
        나만큼 울지 않기를 그대만은
        눈물없이 나 편하게 떠나주기를
        언젠가 다시 돌아올 그대라는 걸 알기에
    """.trimIndent())
    }
}

@Composable
fun ContentIcon() {
    Box(modifier = Modifier
        .size(48.dp, 48.dp)
        .background(Color.Green)) {
        Icon(
            Icons.Filled.Favorite,
            contentDescription = "Favorite",
            modifier = Modifier
                .size(ButtonDefaults.IconSize)
                .align(Alignment.Center)
        )
    }
}

fun Modifier.firstBaselineToTop(
    toTop: Dp
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBL = placeable[FirstBaseline]

    val placeY = toTop.roundToPx() - firstBL
    val height =placeable.height + placeY

    layout(placeable.width, height) {
        placeable.placeRelative(0, placeY)
    }
}


@Composable
fun Custom(content: @Composable () -> Unit) {

}