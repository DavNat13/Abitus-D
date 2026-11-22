package com.abitusd.app.feature.profile.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppChip
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.i18n.AppLanguage

/** App language (Spanish by default). Changing it restarts the screen in the new language. */
@Composable
fun LanguagePicker() {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val current = AppLanguage.saved(context)
    SectionHeader(stringResource(R.string.profile_language))
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        AppLanguage.SUPPORTED.forEach { language ->
            val label = if (language == AppLanguage.SPANISH) R.string.language_spanish else R.string.language_english
            AppChip(stringResource(label), language == current, {
                if (language != current) {
                    AppLanguage.save(context, language)
                    activity?.recreate()
                }
            })
        }
    }
}
