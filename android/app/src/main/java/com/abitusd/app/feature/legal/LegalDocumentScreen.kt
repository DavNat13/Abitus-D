package com.abitusd.app.feature.legal

import androidx.annotation.RawRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.theme.AppTheme

enum class LegalDocument(
    @param:RawRes val raw: Int,
) {
    PRIVACY(R.raw.privacy_policy),
    TERMS(R.raw.terms),
    CREDITS(R.raw.third_party),
}

/** Shows a document from legal/ (copied at build time) with headings and paragraphs. */
@Composable
fun LegalDocumentScreen(
    document: LegalDocument,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val lines =
        remember(document) {
            context.resources
                .openRawResource(document.raw)
                .bufferedReader()
                .use { it.readLines() }
                .filter { it.isNotBlank() && !it.startsWith("|---") }
                .map(::plainLine)
        }
    val title = lines.firstOrNull()?.removePrefix("# ").orEmpty()
    FormScreen(title = title, onBack = onBack) {
        lines.drop(1).forEach { line -> LegalLine(line) }
    }
}

/** Table rows become one line of text; code marks are removed. */
private fun plainLine(line: String): String {
    val text = line.replace("`", "")
    if (!text.startsWith("|")) return text
    return text
        .split('|')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" - ")
}

@Composable
private fun LegalLine(line: String) {
    val colors = AppTheme.colors
    when {
        line.startsWith("## ") -> {
            Text(
                text = line.removePrefix("## "),
                style = AppTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
        }

        line.startsWith("- ") -> {
            Text(text = "• " + line.removePrefix("- "), style = AppTheme.typography.body, color = colors.textPrimary)
        }

        else -> {
            Text(text = line, style = AppTheme.typography.body, color = colors.textPrimary)
        }
    }
}
