/**
 * Copyright (c) Microblink. Modifications are allowed under the terms of the
 * license for files located in the UX/UI lib folder.
 */

package com.microblink.blinkidverify.ux.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.microblink.blinkidverify.ux.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreens(
    onChangeHelpScreensState: (Boolean) -> Unit
) {
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            onChangeHelpScreensState(false)
        },
        sheetMaxWidth = 800.dp,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        val pagerState = rememberPagerState(pageCount = {
            HelpScreen.Id.helpDialogPages.size
        })
        Column(modifier = Modifier.height(if (orientation == Configuration.ORIENTATION_PORTRAIT) 520.dp else 240.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        if (pagerState.canScrollBackward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        } else onChangeHelpScreensState(false)

                    }) {
                    Text(
                        if (pagerState.canScrollBackward) stringResource(R.string.mb_blinkid_verify_back) else stringResource(
                            R.string.mb_blinkid_verify_skip
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        if (pagerState.canScrollForward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else onChangeHelpScreensState(false)
                    }) {
                    Text(
                        if (pagerState.canScrollForward) stringResource(R.string.mb_blinkid_verify_next) else stringResource(
                            R.string.mb_blinkid_verify_done
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            when (orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    HelpScreensContentLandscape(
                        pagerState = pagerState
                    )
                }

                else -> {
                    HelpScreensContentPortrait(
                        pagerState = pagerState
                    )
                }
            }
        }
    }
}

@Composable
fun HelpScreensContentPortrait(
    pagerState: PagerState
) {
    Column {
        HorizontalPager(
            modifier = Modifier
                .weight(0.80f)
                .fillMaxWidth(),
            state = pagerState
        ) { pageNum ->
            val page = HelpScreen.Id.helpDialogPages[pageNum]
            Column(Modifier.fillMaxHeight()) {
                Column(Modifier.weight(0.4f)) {
                    Image(
                        ContextCompat.getDrawable(
                            LocalContext.current,
                            page.pageImage
                        )?.toBitmap()?.asImageBitmap()!!,
                        // TODO: accessibility
                        stringResource(page.pageTitle),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(bottom = 20.dp),
                ) {
                    Spacer(Modifier.height(20.dp))
                    // TODO: accessibility
                    Column(
                        modifier = Modifier
                            .padding(start = 40.dp, end = 40.dp)
                            .verticalScroll(rememberScrollState())
                            .weight(weight = 0.6f, fill = false)
                    ) {
                        Text(
                            text = stringResource(page.pageTitle),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            modifier = Modifier.fillMaxHeight(),
                            text = stringResource(page.pageMessage),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .weight(0.15f)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.4f
                    )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

@Composable
fun HelpScreensContentLandscape(
    pagerState: PagerState
) {
    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier
                .weight(0.80f)
                .fillMaxWidth(),
            state = pagerState
        ) { pageNum ->
            val page = HelpScreen.Id.helpDialogPages[pageNum]
            Row(Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)) {
                Column(Modifier.weight(0.35f)) {
                    Image(
                        ContextCompat.getDrawable(
                            LocalContext.current,
                            page.pageImage
                        )?.toBitmap()?.asImageBitmap()!!,
                        stringResource(page.pageTitle),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .weight(0.65f)
                ) {
                    Text(
                        text = stringResource(page.pageTitle),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            modifier = Modifier.fillMaxHeight(),
                            text = stringResource(page.pageMessage),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .weight(0.20f)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.4f
                    )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

enum class HelpScreen(
    val onboardingDialogPage: HelpScreenPage, val helpDialogPages: List<HelpScreenPage>,
) {
    Id(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_id,
            pageTitle = R.string.mb_blinkid_verify_onboarding_dialog_title,
            pageMessage = R.string.mb_blinkid_verify_onboarding_dialog_message
        ), helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_one,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title1,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg1
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_two,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title2,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg2
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_blur,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title3,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg3
            )
        )
    ),
    Barcode(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_barcode,
            pageTitle = R.string.mb_blinkid_verify_onboarding_dialog_title_barcode,
            pageMessage = R.string.mb_blinkid_verify_onboarding_dialog_message_barcode
        ), helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_barcode_page_one,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title1_barcode,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg1_barcode
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_barcode_page_two,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title2,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg2
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_blur,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title3,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg3
            )
        )
    ),
    Mrz(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_mrz,
            pageTitle = R.string.mb_blinkid_verify_onboarding_dialog_title_mrz,
            pageMessage = R.string.mb_blinkid_verify_onboarding_dialog_message_mrz
        ), helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_mrz_page_one,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title1_mrz,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg1_mrz
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_mrz_page_two,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title2,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg2
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_blur,
                pageTitle = R.string.mb_blinkid_verify_help_dialog_title3,
                pageMessage = R.string.mb_blinkid_verify_help_dialog_msg3
            )
        )
    )
}

data class HelpScreenPage(
    @DrawableRes val pageImage: Int,
    @StringRes val pageTitle: Int,
    @StringRes val pageMessage: Int
)
