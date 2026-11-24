package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.workout.data.SessionEditor
import kotlinx.coroutines.launch

/** Tools of one exercise (10f): warm-up sets, swap for a related exercise, discomfort and plates. */
@Composable
fun ExerciseTools(
    block: ExerciseBlock,
    editor: SessionEditor,
    onPlates: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var swapping by rememberSaveable { mutableStateOf(false) }
    val exercise = block.exercise
    SectionCard(stringResource(R.string.workout_tools), icon = AppIcons.Tip) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            AppButton(stringResource(R.string.tools_warmup), {
                scope.launch { editor.addWarmups(exercise.id) }
            }, Modifier.weight(1f), AppButtonStyle.Tonal)
            AppButton(
                stringResource(R.string.tools_swap),
                { swapping = true },
                Modifier.weight(1f),
                AppButtonStyle.Tonal,
            )
        }
        val zones = ZONES.map { (key, label) -> ChoiceOption(key, stringResource(label)) }
        AppDropdown(
            stringResource(R.string.tools_discomfort),
            zones,
            exercise.discomfort.ifBlank { null },
            { zone -> scope.launch { editor.annotate(exercise.id, exercise.notes, zone.orEmpty()) } },
            noneLabel = stringResource(R.string.tools_no_discomfort),
        )
        AppButton(
            stringResource(R.string.plate_calculator),
            onPlates,
            style = AppButtonStyle.Text,
            icon = AppIcons.Calculator,
        )
    }
    if (swapping) {
        AlternativesSheet(exercise.exerciseId, onPick = { id ->
            swapping = false
            scope.launch { editor.replaceExercise(exercise.id, id) }
        }, onDismiss = { swapping = false })
    }
}

private val ZONES =
    listOf(
        "neck" to R.string.zone_neck,
        "shoulder_left" to R.string.zone_shoulder_left,
        "shoulder_right" to R.string.zone_shoulder_right,
        "elbow" to R.string.zone_elbow,
        "wrist" to R.string.zone_wrist,
        "lower_back" to R.string.zone_lower_back,
        "hip" to R.string.zone_hip,
        "knee" to R.string.zone_knee,
        "ankle" to R.string.zone_ankle,
    )
