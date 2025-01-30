package com.microblink.blinkidverify.ux.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.microblink.blinkidverify.ux.R
import com.microblink.blinkidverify.ux.theme.Cobalt
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreens(
    onChangeHelpScreensState: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            onChangeHelpScreensState(false)
        },
        sheetState = bottomSheetState,
        containerColor = Color.White
    ) {
        val pagerState = rememberPagerState(pageCount = {
            HelpScreen.Id.helpDialogPages.size
        })
        Column(modifier = Modifier.height(600.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Cobalt
                    ),
                    onClick = {
                        if (pagerState.canScrollBackward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }

                    }) {
                    Text(
                        stringResource(R.string.mb_back),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Cobalt
                    ),
                    onClick = {
                        if (pagerState.canScrollForward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else onChangeHelpScreensState(false)
                    }) {
                    Text(
                        if (pagerState.canScrollForward) stringResource(R.string.mb_next) else stringResource(
                            R.string.mb_done
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            HorizontalPager(
                modifier = Modifier
                    .weight(0.80f)
                    .fillMaxWidth(),
                state = pagerState
            ) { pageNum ->
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 20.dp),
                ) {
                    val page = HelpScreen.Id.helpDialogPages.get(pageNum)
                    Image(
                        // TODO: base this on Recognizer settings
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
                            .weight(0.4f)
                            .align(Alignment.CenterHorizontally),
                    )
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
                            // TODO: customizable
                            color = Cobalt
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            modifier = Modifier.fillMaxHeight(),
                            text = stringResource(page.pageMessage),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                            color = Color.Black
                        )
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
                    // TODO: customizable colors
                    val color =
                        if (pagerState.currentPage == iteration) Cobalt else Cobalt.copy(alpha = 0.4f)
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
}

enum class HelpScreen(
    val onboardingDialogPage: HelpScreenPage, val helpDialogPages: List<HelpScreenPage>
) {
    Id(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_id,
            pageTitle = R.string.mb_blinkid_onboarding_dialog_title,
            pageMessage = R.string.mb_blinkid_onboarding_dialog_message
        ), helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_one,
                pageTitle = R.string.mb_blinkid_help_dialog_title1,
                pageMessage = R.string.mb_blinkid_help_dialog_msg1
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_page_two,
                pageTitle = R.string.mb_blinkid_help_dialog_title2,
                pageMessage = R.string.mb_blinkid_help_dialog_msg2
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_blur,
                pageTitle = R.string.mb_blinkid_help_dialog_title3,
                pageMessage = R.string.mb_blinkid_help_dialog_msg3
            )
        )
    ),
    Barcode(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_barcode,
            pageTitle = R.string.mb_blinkid_onboarding_dialog_title_barcode,
            pageMessage = R.string.mb_blinkid_onboarding_dialog_message_barcode
        ), helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_barcode_page_one,
                pageTitle = R.string.mb_blinkid_help_dialog_title1_barcode,
                pageMessage = R.string.mb_blinkid_help_dialog_msg1_barcode
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_barcode_page_two,
                pageTitle = R.string.mb_blinkid_help_dialog_title2,
                pageMessage = R.string.mb_blinkid_help_dialog_msg2
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_blur,
                pageTitle = R.string.mb_blinkid_help_dialog_title3,
                pageMessage = R.string.mb_blinkid_help_dialog_msg3
            )
        )
    ),
    Mrz(
        onboardingDialogPage = HelpScreenPage(
            pageImage = R.drawable.mb_blinkid_onboarding_mrz,
            pageTitle = R.string.mb_blinkid_onboarding_dialog_title_mrz,
            pageMessage = R.string.mb_blinkid_onboarding_dialog_message_mrz
        ), helpDialogPages = listOf(
            HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_mrz_page_one,
                pageTitle = R.string.mb_blinkid_help_dialog_title1_mrz,
                pageMessage = R.string.mb_blinkid_help_dialog_msg1_mrz
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_mrz_page_two,
                pageTitle = R.string.mb_blinkid_help_dialog_title2,
                pageMessage = R.string.mb_blinkid_help_dialog_msg2
            ), HelpScreenPage(
                pageImage = R.drawable.mb_blinkid_help_id_blur,
                pageTitle = R.string.mb_blinkid_help_dialog_title3,
                pageMessage = R.string.mb_blinkid_help_dialog_msg3
            )
        )
    )
}

data class HelpScreenPage(
    @DrawableRes val pageImage: Int,
    @StringRes val pageTitle: Int,
    @StringRes val pageMessage: Int
)
