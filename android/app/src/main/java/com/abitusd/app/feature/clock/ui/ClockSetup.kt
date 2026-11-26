package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockPresets
import com.abitusd.app.feature.clock.domain.ClockSpec

/** Pick a preset or saved profile, adjust it, start it or save it as a profile. */
@Composable
fun ClockSetup(
    s: ClockUiState,
    vm: ClockViewModel,
) {
    val d = s.draft
    var naming by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf(false) }
    val presetName = d.presetKey?.let { stringResource(ClockLabels.preset(it)) }
    val runName = presetName ?: d.name
    SectionCard(stringResource(R.string.clock_choose), icon = AppIcons.Timer) {
        val options =
            ClockPresets.all.map { ChoiceOption("p:${it.key}", stringResource(ClockLabels.preset(it.key))) } +
                s.profiles.map { ChoiceOption("u:${it.id}", it.name) }
        val selected = d.presetKey?.let { "p:$it" } ?: "u:${d.profileId}"
        AppDropdown(stringResource(R.string.clock_mode), options, selected, { v ->
            when {
                v == null -> Unit
                v.startsWith("p:") -> vm.choosePreset(v.removePrefix("p:"))
                else -> s.profiles.firstOrNull { it.id == v.removePrefix("u:") }?.let(vm::chooseProfile)
            }
        })
        SpecEditor(d.spec, !d.mode.countsUp, d.mode == ClockMode.CUSTOM, vm::edit)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        AppButton(
            stringResource(R.string.clock_start),
            { vm.start(runName) },
            Modifier.weight(1f),
            icon = AppIcons.Start,
        )
        AppButton(stringResource(R.string.clock_save), {
            naming = true
        }, Modifier.weight(1f), AppButtonStyle.Tonal, icon = AppIcons.Favorite)
    }
    if (d.profileId != null) {
        AppButton(stringResource(R.string.clock_delete), {
            deleting = true
        }, style = AppButtonStyle.Destructive, icon = AppIcons.Delete)
    }
    if (naming) {
        var name by rememberSaveable { mutableStateOf(runName) }
        AppBottomSheet(stringResource(R.string.clock_save), { naming = false }) {
            AppTextField(name, { name = it.take(MAX_NAME) }, stringResource(R.string.clock_profile_name))
            AppButton(stringResource(R.string.clock_save), {
                vm.saveProfile(name)
                naming = false
            }, enabled = name.isNotBlank())
        }
    }
    if (deleting) {
        ConfirmDialog(
            title = stringResource(R.string.clock_delete),
            message = stringResource(R.string.clock_delete_note, d.name),
            confirmLabel = stringResource(R.string.clock_delete),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = {
                d.profileId?.let(vm::deleteProfile)
                deleting = false
            },
            onDismiss = { deleting = false },
        )
    }
}

private const val MAX_NAME = 60
