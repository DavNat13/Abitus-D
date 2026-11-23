package com.abitusd.app.feature.workout.ui.active

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.SetType
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons

/** Edits one set with large steppers (05: big numbers, 48 dp or larger plus and minus). */
@Composable
fun SetEditorSheet(
    set: SetEntity,
    number: Int,
    onChange: ((SetEntity) -> SetEntity) -> Unit,
    onType: (String) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    val minus = stringResource(R.string.workout_decrease)
    val plus = stringResource(R.string.workout_increase)
    AppBottomSheet(stringResource(R.string.workout_edit_set, number), onDismiss) {
        val load = set.loadKg ?: 0.0
        Stepper(
            label = stringResource(R.string.workout_load_kg),
            value = if (load > 0.0) formatKg(load) else stringResource(R.string.workout_bodyweight),
            minusDescription = minus,
            plusDescription = plus,
            onMinus = { onChange { it.copy(loadKg = (load - LOAD_STEP).takeIf { v -> v > 0.0 }) } },
            onPlus = { onChange { it.copy(loadKg = (load + LOAD_STEP).coerceAtMost(MAX_LOAD)) } },
        )
        val reps = set.reps ?: 0
        Stepper(
            label = stringResource(R.string.workout_reps_long),
            value = reps.toString(),
            minusDescription = minus,
            plusDescription = plus,
            onMinus = { onChange { it.copy(reps = (reps - 1).coerceAtLeast(0)) } },
            onPlus = { onChange { it.copy(reps = (reps + 1).coerceAtMost(MAX_REPS)) } },
        )
        val rpeOptions = RPE_VALUES.map { ChoiceOption(it, formatKg(it)) }
        AppDropdown(stringResource(R.string.routine_rpe), rpeOptions, set.rpe, { v ->
            onChange {
                it.copy(rpe = v)
            }
        }, noneLabel = stringResource(R.string.workout_not_set))
        val types = SetType.ALL.map { ChoiceOption(it, stringResource(typeLabel(it))) }
        AppDropdown(stringResource(R.string.workout_set_type), types, set.setType, { v -> v?.let(onType) })
        AppButton(stringResource(R.string.workout_done), onDismiss, icon = AppIcons.Check)
        AppButton(stringResource(R.string.workout_remove_set), {
            onRemove()
            onDismiss()
        }, style = AppButtonStyle.Destructive, icon = AppIcons.Delete)
    }
}

private const val LOAD_STEP = 2.5
private const val MAX_LOAD = 2000.0
private const val MAX_REPS = 1000
private val RPE_VALUES = listOf(6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0)
