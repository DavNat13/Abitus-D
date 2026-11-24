package com.abitusd.app.feature.train.ui.programs

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.BlockGoal
import com.abitusd.app.core.database.planning.BlockPhase
import com.abitusd.app.core.database.planning.Progression
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.domain.BlockSpec

/** One cell per week, coloured by the phase of its block; the current week gets an outline. */
@Composable
fun BlockTimeline(
    blocks: List<BlockSpec>,
    currentWeek: Int?,
    description: String,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val weeks = blocks.sortedBy { it.position }.flatMap { block -> List(block.weeks) { block.phase } }
    Row(
        modifier.fillMaxWidth().semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(CELL_GAP),
    ) {
        weeks.forEachIndexed { index, phase ->
            val cell = Modifier.weight(1f).height(CELL_HEIGHT).background(phaseColor(phase), AppShapes.pill)
            Box(if (index == currentWeek) cell.border(AppSizes.borderFocus, colors.ink, AppShapes.pill) else cell)
        }
    }
}

@Composable
fun phaseColor(phase: String): Color {
    val colors = AppTheme.colors
    return when (phase) {
        BlockPhase.INTENSIFICATION -> colors.navy
        BlockPhase.DELOAD -> colors.success
        BlockPhase.TEST -> colors.warning
        else -> colors.primary
    }
}

@StringRes
fun phaseLabel(phase: String): Int =
    when (phase) {
        BlockPhase.INTENSIFICATION -> R.string.phase_intensification
        BlockPhase.DELOAD -> R.string.phase_deload
        BlockPhase.TEST -> R.string.phase_test
        else -> R.string.phase_accumulation
    }

@DrawableRes
fun phaseIcon(phase: String): Int =
    when (phase) {
        BlockPhase.INTENSIFICATION -> AppIcons.Train
        BlockPhase.DELOAD -> AppIcons.Rest
        BlockPhase.TEST -> AppIcons.Check
        else -> AppIcons.Progression
    }

@StringRes
fun progressionLabel(progression: String): Int =
    when (progression) {
        Progression.DOUBLE -> R.string.progression_double
        Progression.PERCENT -> R.string.progression_percent
        Progression.RPE -> R.string.progression_rpe
        else -> R.string.progression_none
    }

@StringRes
fun goalLabel(goal: String): Int =
    when (goal) {
        BlockGoal.STRENGTH -> R.string.goal_strength
        BlockGoal.HYPERTROPHY -> R.string.goal_hypertrophy
        BlockGoal.ENDURANCE -> R.string.goal_endurance
        BlockGoal.SKILL -> R.string.goal_skill
        else -> R.string.goal_general
    }

private val CELL_HEIGHT = 12.dp
private val CELL_GAP = 4.dp
