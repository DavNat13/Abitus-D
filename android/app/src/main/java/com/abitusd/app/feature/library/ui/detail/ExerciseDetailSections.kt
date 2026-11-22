package com.abitusd.app.feature.library.ui.detail

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.abitusd.app.core.designsystem.component.IconRow
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.NumberedStep
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Card with an icon and a paragraph; hidden when empty. */
@Composable
fun DetailParagraph(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    text: String,
) {
    if (text.isBlank()) return
    SectionCard(stringResource(title), icon = icon) {
        Text(text, style = AppTheme.typography.body, color = AppTheme.colors.textPrimary)
    }
}

/** Primary muscles emphasized, secondary soft, then equipment, in one card. */
@Composable
fun MusclesCard(
    primary: List<String>,
    secondary: List<String>,
    equipment: List<String>,
) {
    if (primary.isEmpty() && secondary.isEmpty() && equipment.isEmpty()) return
    SectionCard(stringResource(R.string.detail_muscles_equipment), icon = AppIcons.Muscles) {
        PillGroup(R.string.detail_primary_muscles, primary, emphasized = true)
        PillGroup(R.string.detail_secondary_muscles, secondary, emphasized = false)
        PillGroup(R.string.detail_equipment, equipment, emphasized = false)
    }
}

@Composable
private fun PillGroup(
    @StringRes title: Int,
    items: List<String>,
    emphasized: Boolean,
) {
    if (items.isEmpty()) return
    SectionHeader(stringResource(title))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        items.forEach { InfoPill(it, emphasized = emphasized) }
    }
}

/** Execution steps with number badges. */
@Composable
fun StepsCard(steps: List<String>) {
    if (steps.isEmpty()) return
    SectionCard(stringResource(R.string.detail_execution), icon = AppIcons.Steps) {
        steps.forEachIndexed { index, step -> NumberedStep(index + 1, step) }
    }
}

/** Cues (check icon, green) or mistakes (close icon, error). */
@Composable
fun IconListCard(
    @StringRes title: Int,
    items: List<String>,
    positive: Boolean,
) {
    if (items.isEmpty()) return
    val colors = AppTheme.colors
    SectionCard(stringResource(title), icon = if (positive) AppIcons.Tip else AppIcons.Mistake) {
        items.forEach {
            if (positive) IconRow(AppIcons.Check, it, colors.successText) else IconRow(AppIcons.Close, it, colors.error)
        }
    }
}

/** Related exercises inside one card, each row opens its detail. */
@Composable
fun RelatedExercises(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    items: List<RelatedExercise>,
    onOpen: (String) -> Unit,
) {
    if (items.isEmpty()) return
    val colors = AppTheme.colors
    SectionCard(stringResource(title), icon = icon) {
        items.forEachIndexed { index, related ->
            if (index > 0) HorizontalDivider(color = colors.outline)
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppSizes.touchTarget)
                    .clickable(role = Role.Button) { onOpen(related.id) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    related.name,
                    style = AppTheme.typography.body,
                    color = colors.primary,
                    modifier = Modifier.weight(1f),
                )
                Icon(painterResource(AppIcons.ChevronRight), contentDescription = null, tint = colors.textSecondary)
            }
        }
    }
}
