package com.abitusd.app.feature.progress.ui.body

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.VolumeBars
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Named, tappable alternative to the drawing, including muscles that are not visible in it. */
@Composable
internal fun BodyVolumeList(
    state: BodyState,
    onSelect: (String) -> Unit,
) {
    SectionCard(stringResource(R.string.body_list), icon = AppIcons.Steps) {
        if (state.byKey.isEmpty()) {
            Text(stringResource(R.string.body_empty), style = AppTheme.typography.body)
        }
        state.byKey.entries.sortedByDescending { it.value.perWeek }.forEach { (key, muscle) ->
            TextButton(onClick = { onSelect(key) }) { Text(muscle.name) }
            VolumeBars(
                listOf(muscle.name to muscle.perWeek),
                targets = listOf(state.minSets..state.maxSets),
            )
            val level = status(muscle.perWeek, state.minSets, state.maxSets)
            InfoPill(
                stringResource(
                    STATUS_LABELS.first { it.first == level }.second,
                    if (level == LOW) state.minSets else state.maxSets,
                ),
            )
        }
    }
}
