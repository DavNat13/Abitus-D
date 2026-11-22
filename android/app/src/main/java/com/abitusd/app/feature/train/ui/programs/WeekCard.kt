package com.abitusd.app.feature.train.ui.programs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ProgramDayEntity
import com.abitusd.app.core.designsystem.component.IconBadge
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

private const val DAYS = 7

/** One week of the program: seven tappable rows showing the routine, rest or nothing. */
@Composable
fun WeekCard(
    week: Int,
    days: List<ProgramDayEntity>,
    routineNames: Map<String, String>,
    onEditDay: (Int) -> Unit,
) {
    val colors = AppTheme.colors
    SectionCard(stringResource(R.string.program_week, week + 1), icon = AppIcons.Calendar) {
        repeat(DAYS) { day ->
            if (day > 0) HorizontalDivider(color = colors.outline)
            val cell = days.firstOrNull { it.weekIndex == week && it.dayIndex == day }
            val (icon, label) =
                when {
                    cell == null -> null to stringResource(R.string.program_unassigned)
                    cell.isRest -> AppIcons.Rest to stringResource(R.string.program_rest)
                    else -> AppIcons.Train to routineNames[cell.routineId].orEmpty()
                }
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppSizes.touchTarget)
                    .clickable(role = Role.Button) { onEditDay(day) }
                    .padding(vertical = AppSpacing.xxs),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.program_day, day + 1),
                    style = AppTheme.typography.label,
                    color = colors.textSecondary,
                )
                if (icon != null) IconBadge(icon, size = AppSizes.stepBadge)
                Text(
                    label,
                    style = AppTheme.typography.body,
                    color = if (cell == null) colors.textSecondary else colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Icon(painterResource(AppIcons.ChevronRight), contentDescription = null, tint = colors.textSecondary)
            }
        }
    }
}
