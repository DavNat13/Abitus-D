package com.abitusd.app.feature.progress.ui.body

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Color swatch plus words for each status, so the map never relies on color alone. */
@Composable
internal fun Legend(
    min: Int,
    max: Int,
) {
    val colors = AppTheme.colors
    STATUS_LABELS.forEach { (st, label) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        ) {
            androidx.compose.foundation.Canvas(
                androidx.compose.ui.Modifier
                    .size(SWATCH.dp),
            ) { drawCircle(statusColor(st, colors)) }
            Text(stringResource(label, if (st == LOW) min else max), style = AppTheme.typography.body)
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        androidx.compose.foundation.Canvas(
            androidx.compose.ui.Modifier
                .size(SWATCH.dp),
        ) { drawCircle(colors.outline) }
        Text(stringResource(R.string.body_no_sets), style = AppTheme.typography.body)
    }
}

private const val SWATCH = 14
