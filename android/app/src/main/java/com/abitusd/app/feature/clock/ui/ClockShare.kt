package com.abitusd.app.feature.clock.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.clock.data.ClockProfiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClockShareViewModel
    @Inject
    constructor(
        private val profiles: ClockProfiles,
    ) : ViewModel() {
        fun import(text: String) {
            viewModelScope.launch { profiles.importJson(text) }
        }
    }

/** Share the selected profile as a file-like text through the system sheet, or import one (11e). */
@Composable
fun ClockShare(
    draft: ClockDraft,
    vm: ClockShareViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val picker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val text =
                uri?.let {
                    context.contentResolver.openInputStream(it)?.use { s ->
                        s.readBytes().decodeToString()
                    }
                }
            if (text != null && text.length <= MAX_CHARS) vm.import(text)
        }
    val chooser = stringResource(R.string.clock_share)
    val fallback = stringResource(R.string.clock_title)
    val name = draft.name.ifBlank { fallback }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        AppButton(chooser, {
            val json = ClockProfiles.exportJson(name, draft.mode, draft.spec)
            val send =
                Intent(Intent.ACTION_SEND)
                    .setType("application/json")
                    .putExtra(Intent.EXTRA_TEXT, json)
                    .putExtra(Intent.EXTRA_TITLE, "$name.abitusd.json")
            context.startActivity(Intent.createChooser(send, chooser))
        }, Modifier.weight(1f), AppButtonStyle.Secondary, icon = AppIcons.Share)
        AppButton(stringResource(R.string.clock_import), {
            picker.launch("*/*")
        }, Modifier.weight(1f), AppButtonStyle.Secondary)
    }
}

private const val MAX_CHARS = 8_000
