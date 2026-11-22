package com.abitusd.app.feature.library.ui.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

@Composable
private fun SectionTitle(
    @StringRes title: Int,
) {
    Text(
        text = stringResource(title),
        style = AppTheme.typography.titleMedium,
        color = AppTheme.colors.textPrimary,
        modifier = Modifier.semantics { heading() },
    )
}

/** Titled paragraph; hidden when the text is empty. */
@Composable
fun DetailParagraph(
    @StringRes title: Int,
    text: String,
) {
    if (text.isBlank()) return
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
        SectionTitle(title)
        Text(text = text, style = AppTheme.typography.body, color = AppTheme.colors.textPrimary)
    }
}

/** Titled list, numbered for steps; hidden when empty. */
@Composable
fun DetailList(
    @StringRes title: Int,
    items: List<String>,
    numbered: Boolean,
) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
        SectionTitle(title)
        items.forEachIndexed { index, item ->
            val prefix = if (numbered) "${index + 1}. " else "• "
            Text(text = prefix + item, style = AppTheme.typography.body, color = AppTheme.colors.textPrimary)
        }
    }
}

/** Related exercises as text buttons that open their detail. */
@Composable
fun RelatedExercises(
    @StringRes title: Int,
    items: List<RelatedExercise>,
    onOpen: (String) -> Unit,
) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
        SectionTitle(title)
        items.forEach { related ->
            AppButton(related.name, { onOpen(related.id) }, style = AppButtonStyle.Text)
        }
    }
}
