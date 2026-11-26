package com.abitusd.app.feature.progress.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons

/** Last measurements; tapping one asks before deleting it everywhere. */
@Composable
internal fun HistoryCard(
    s: MeasurementsState,
    onDelete: (String) -> Unit,
) {
    val f = fmt()
    var deleting by rememberSaveable { mutableStateOf<String?>(null) }
    SectionCard(stringResource(R.string.measure_history), icon = AppIcons.History) {
        if (s.rows.isEmpty()) Hint(R.string.measure_history_empty)
        s.rows.reversed().take(TOP).forEach { r ->
            val parts =
                listOfNotNull(
                    r.weightKg?.let { kg(it) },
                    r.waistM?.let {
                        stringResource(
                            R.string.measure_waist_short,
                            f(it * CM, 1),
                        )
                    },
                )
            AppButton("${r.measuredOn}  ${parts.joinToString("  ")}", {
                deleting = r.id
            }, style = AppButtonStyle.Text, icon = AppIcons.Delete)
        }
    }
    deleting?.let { id ->
        ConfirmDialog(
            title = stringResource(R.string.measure_delete),
            message = stringResource(R.string.measure_delete_note),
            confirmLabel = stringResource(R.string.measure_delete),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = {
                onDelete(id)
                deleting = null
            },
            onDismiss = { deleting = null },
        )
    }
}

private const val TOP = 8
private const val CM = 100.0

@Composable
private fun fmt(): (Double, Int) -> String {
    val locale = LocalConfiguration.current.locales[0]
    return { v, d -> String.format(locale, "%.${d}f", v) }
}
