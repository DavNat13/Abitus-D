package com.abitusd.app.feature.clock.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

/** "Aprende" glossary (11f): the training terms the app uses, in plain language. */
@Composable
fun GlossaryScreen(onBack: () -> Unit) {
    FormScreen(title = stringResource(R.string.glossary_title), onBack = onBack) {
        TERMS.forEach { (term, text) ->
            SectionCard(stringResource(term), icon = AppIcons.Library) {
                Text(stringResource(text), style = AppTheme.typography.body, color = AppTheme.colors.textSecondary)
            }
        }
    }
}

private val TERMS =
    listOf(
        R.string.glossary_rpe to R.string.glossary_rpe_d,
        R.string.glossary_rir to R.string.glossary_rir_d,
        R.string.glossary_1rm to R.string.glossary_1rm_d,
        R.string.glossary_e1rm to R.string.glossary_e1rm_d,
        R.string.glossary_volume to R.string.glossary_volume_d,
        R.string.glossary_deload to R.string.glossary_deload_d,
        R.string.glossary_meso to R.string.glossary_meso_d,
        R.string.glossary_progression to R.string.glossary_progression_d,
        R.string.glossary_superset to R.string.glossary_superset_d,
        R.string.glossary_srpe to R.string.glossary_srpe_d,
        R.string.glossary_emom to R.string.glossary_emom_d,
        R.string.glossary_amrap to R.string.glossary_amrap_d,
        R.string.glossary_tabata to R.string.glossary_tabata_d,
        R.string.glossary_fortime to R.string.glossary_fortime_d,
    )
