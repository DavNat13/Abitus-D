package com.abitusd.app.feature.workout.ui.preview

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.ui.programs.phaseIcon
import com.abitusd.app.feature.train.ui.programs.phaseLabel

/** Today's place in the block, the travel or place switch and the exercises it swapped. */
@Composable
fun PlanSection(
    block: PreviewBlock,
    travel: Boolean,
    onTravel: (Boolean) -> Unit,
) {
    val week = block.week
    val icon = week?.let { phaseIcon(it.block.phase) } ?: AppIcons.Alternative
    SectionCard(stringResource(R.string.plan_title), icon = icon) {
        if (week != null) {
            val phase = stringResource(phaseLabel(week.block.phase)).lowercase()
            val number = minOf(week.programWeek + 1, block.totalWeeks)
            InfoPill(stringResource(R.string.blocks_week_of, number, block.totalWeeks, phase), emphasized = true)
            Text(
                stringResource(R.string.plan_progression_applied),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        val label = stringResource(R.string.plan_travel)
        ListItemCard(label, {
            onTravel(!travel)
        }, icon = AppIcons.Alternative, meta = stringResource(R.string.plan_travel_hint)) {
            Switch(travel, onTravel, Modifier.semantics { contentDescription = label })
        }
        block.swaps.forEach { (from, to) ->
            Text(
                stringResource(R.string.plan_swap, from, to),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textPrimary,
            )
        }
        if (block.unfit.isNotEmpty()) {
            StatusBanner(stringResource(R.string.plan_unfit, block.unfit.joinToString()), StatusKind.Warning)
        }
        if (travel && block.swaps.isEmpty() && block.unfit.isEmpty()) {
            Text(
                stringResource(R.string.plan_no_swaps),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}
