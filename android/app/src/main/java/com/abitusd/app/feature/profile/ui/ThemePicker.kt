package com.abitusd.app.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppChip
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.ThemeMode
import com.abitusd.app.core.designsystem.theme.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(
        private val preference: ThemePreference,
    ) : ViewModel() {
        val mode = preference.mode

        fun select(value: ThemeMode) = preference.set(value)
    }

/** Light, dark or system theme; applies at once to the whole app. */
@Composable
fun ThemePicker(viewModel: ThemeViewModel = hiltViewModel()) {
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    SectionHeader(stringResource(R.string.profile_theme))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        ThemeMode.entries.forEach { option ->
            AppChip(stringResource(option.label), option == mode, { viewModel.select(option) })
        }
    }
}
