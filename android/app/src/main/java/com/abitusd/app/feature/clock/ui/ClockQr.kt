package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanOptions

/** QR of a shared timer profile (11e), generated on the phone with ZXing; no service involved. */
@Composable
fun ProfileQrSheet(
    json: String,
    onDismiss: () -> Unit,
) {
    val bitmap = remember(json) { BarcodeEncoder().encodeBitmap(json, BarcodeFormat.QR_CODE, SIZE, SIZE) }
    val description = stringResource(R.string.clock_qr_description)
    AppBottomSheet(stringResource(R.string.clock_qr_show), onDismiss) {
        Image(bitmap.asImageBitmap(), description, Modifier.fillMaxWidth())
    }
}

/** Scanner options: QR only, no beep, with a prompt in the user's language. */
fun qrScanOptions(prompt: String): ScanOptions =
    ScanOptions()
        .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        .setPrompt(prompt)
        .setBeepEnabled(false)
        .setOrientationLocked(false)

private const val SIZE = 720
