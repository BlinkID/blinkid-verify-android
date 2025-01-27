package com.microblink.blinkidverify.ux.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.microblink.blinkidverify.ux.R
import com.microblink.blinkidverify.ux.theme.Cobalt

@Composable
fun OnboardingDialog(onDismissOnboardingDialog: () -> Unit) {
    Dialog(onDismissRequest = onDismissOnboardingDialog) {
        Card(
            modifier = Modifier
                .padding(0.dp)
                .verticalScroll(
                    state = rememberScrollState()
                ),
            // TODO: make customizable?
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier.padding(vertical = 24.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(HelpScreen.Id.onboardingDialogPage.pageTitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = Cobalt
                )
                Spacer(Modifier.height(10.dp))
                Image(

                    // TODO: base this on Recognizer settings
                    ContextCompat.getDrawable(
                        LocalContext.current,
                        HelpScreen.Id.onboardingDialogPage.pageImage
                    )?.toBitmap()?.asImageBitmap()!!,
                    // TODO: accessibility
                    stringResource(R.string.mb_blinkid_onboarding_dialog_title),
                    modifier = Modifier.padding(horizontal = 10.dp),
                )
                // TODO: font colors through customization
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    color = Color.Black,
                    text = stringResource(HelpScreen.Id.onboardingDialogPage.pageMessage),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(20.dp))
                // TODO: add no ripple clickable to the entire material theme
                Button(
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Cobalt
                    ),
                    onClick = {
                        onDismissOnboardingDialog()
                    }) {
                    Text(
                        stringResource(R.string.mb_done),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

        }
    }
}