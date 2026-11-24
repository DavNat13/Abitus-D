package com.abitusd.app.feature.train.ui.programs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.BlockGoal
import com.abitusd.app.core.database.planning.BlockPhase
import com.abitusd.app.core.database.planning.ProgramBlockEntity
import com.abitusd.app.core.database.planning.Progression
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import java.util.Locale

/** Edits one block: name, phase, goal, length and how loads progress week to week. */
@Composable
fun BlockSheet(
    block: ProgramBlockEntity,
    onChange: (ProgramBlockEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember(block.id) { mutableStateOf(block) }
    val minus = stringResource(R.string.workout_decrease)
    val plus = stringResource(R.string.workout_increase)
    AppBottomSheet(stringResource(R.string.blocks_edit), onDismiss) {
        AppTextField(
            draft.name,
            { draft = draft.copy(name = it.take(MAX_NAME)) },
            stringResource(R.string.blocks_name),
            supportingText = stringResource(R.string.blocks_name_hint),
        )
        AppDropdown(
            stringResource(R.string.blocks_phase),
            BlockPhase.ALL.map { ChoiceOption(it, stringResource(phaseLabel(it))) },
            draft.phase,
            { v -> v?.let { draft = draft.copy(phase = it) } },
        )
        AppDropdown(
            stringResource(R.string.blocks_goal),
            BlockGoal.ALL.map { ChoiceOption(it, stringResource(goalLabel(it))) },
            draft.goal,
            { v -> v?.let { draft = draft.copy(goal = it) } },
        )
        Stepper(
            label = stringResource(R.string.blocks_length),
            value = pluralStringResource(R.plurals.blocks_weeks, draft.weeks, draft.weeks),
            minusDescription = minus,
            plusDescription = plus,
            onMinus = { draft = draft.copy(weeks = (draft.weeks - 1).coerceAtLeast(1)) },
            onPlus = { draft = draft.copy(weeks = (draft.weeks + 1).coerceAtMost(MAX_WEEKS)) },
        )
        if (draft.phase != BlockPhase.DELOAD) ProgressionFields(draft, minus, plus) { draft = it }
        BlockSubstitutions(block.programId, block.id)
        AppButton(stringResource(R.string.blocks_save), {
            onChange(draft)
            onDismiss()
        }, icon = AppIcons.Check)
    }
}

@Composable
private fun ProgressionFields(
    draft: ProgramBlockEntity,
    minus: String,
    plus: String,
    onDraft: (ProgramBlockEntity) -> Unit,
) {
    AppDropdown(
        stringResource(R.string.blocks_progression),
        Progression.ALL.map { ChoiceOption(it, stringResource(progressionLabel(it))) },
        draft.progression,
        { v -> v?.let { onDraft(draft.copy(progression = it, step = defaultStep(it))) } },
    )
    val unit =
        when (draft.progression) {
            Progression.DOUBLE -> R.string.blocks_step_kg
            Progression.PERCENT -> R.string.blocks_step_percent
            else -> null
        }
    unit?.let {
        val step = draft.step ?: 0.0
        Stepper(
            label =
                stringResource(
                    if (draft.progression ==
                        Progression.DOUBLE
                    ) {
                        R.string.blocks_step_double
                    } else {
                        R.string.blocks_step_weekly
                    },
                ),
            value = stringResource(it, String.format(Locale.getDefault(), "%.1f", step)),
            minusDescription = minus,
            plusDescription = plus,
            onMinus = { onDraft(draft.copy(step = (step - STEP).coerceAtLeast(STEP))) },
            onPlus = { onDraft(draft.copy(step = (step + STEP).coerceAtMost(MAX_STEP))) },
        )
    }
}

private fun defaultStep(progression: String): Double? =
    when (progression) {
        Progression.DOUBLE -> DEFAULT_KG
        Progression.PERCENT -> DEFAULT_PERCENT
        else -> null
    }

private const val MAX_NAME = 60
private const val MAX_WEEKS = 8
private const val STEP = 0.5
private const val MAX_STEP = 20.0
private const val DEFAULT_KG = 2.5
private const val DEFAULT_PERCENT = 2.5
