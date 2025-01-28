package com.microblink.blinkidverify.sample.ui.result

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.microblink.blinkidverify.sample.R
import com.microblink.blinkidverify.sample.ui.theme.Cobalt
import com.microblink.blinkidverify.sample.ui.theme.Cobalt400
import com.microblink.blinkidverify.sample.ui.theme.Cobalt50
import com.microblink.blinkidverify.sample.ui.theme.Cobalt800
import com.microblink.blinkidverify.sample.ui.theme.DeepBlue
import com.microblink.blinkidverify.sample.ui.theme.ResultItemTitleText
import com.microblink.blinkidverify.sample.ui.theme.ResultItemValueText
import com.microblink.blinkidverify.sample.ui.theme.AppBarTitleText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleResultScreen(
    results: List<ResultItem>,
    onNavigateUp: () -> Unit
) {
    BackHandler(true, onNavigateUp)
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_verification_results), style = AppBarTitleText)
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Cobalt800,
                    titleContentColor = Color.White
                )
            )
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize(),
                color = Cobalt50
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cobalt50)
                ) {
                    if (results.isNotEmpty()) {
                        items(items = results, itemContent = {
                            ResultRow(node = it, level = 0, onClick = { clipboardManager.setText(AnnotatedString(it.value.toString())) })
                        })
                    }
                }
            }
        })
}

@Composable
fun ResultRow(
    node: ResultItem,
    level: Int,
    onClick: () -> Unit,
    showDivider: Boolean = true,
) {
    var expanded by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .animateContentSize(
                spring(
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = IntSize.VisibilityThreshold,
                    dampingRatio = Spring.DampingRatioNoBouncy
                )
            )
    ) {
        ResultRowContent(node, level, expanded, showDivider) {
            expanded = !expanded
            onClick()
        }
        if (expanded) {
            node.children.forEach {
                ResultRow(
                    node = it, level = level + 1,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun ResultRowContent(
    node: ResultItem,
    level: Int,
    isExpanded: Boolean,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    node.value?.let {
        val interactionSource = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(start = (10 * level).dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onClick()
                },
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(0.85f)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = if (node.value != "") 4.dp else 0.dp),
                    text = stringResource(node.title).uppercase(),
                    style = ResultItemTitleText,
                    color = if (level == 0) Cobalt800 else if (level == 1) Cobalt else Cobalt400
                )
                if (node.value != "") Text(
                    modifier = Modifier,
                    text = node.value,
                    style = ResultItemValueText,
                    color = Color.Black
                )
            }
            if (node.children.isNotEmpty()) {
                val chevronRotation by animateFloatAsState(
                    targetValue = if (isExpanded) 90f else 0f,
                    label = ""
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.15f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .rotate(chevronRotation),
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = null
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = DeepBlue,
                thickness = 1.dp
            )
        }
    }
}

data class ResultItem(
    @StringRes val title: Int,
    val value: String?,
    val children: List<ResultItem> = emptyList(),
    @StringRes val description: Int? = null
)

@Composable
@Preview
fun SampleResultScreenPreview() {
    SampleResultScreen(
        results = listOf(
            ResultItem(
                title = R.string.title_verification_results,
                value = "Test value",
                description = R.string.verify_result_hand_card_presence_description
            )
        ),
        onNavigateUp = {}
    )
}