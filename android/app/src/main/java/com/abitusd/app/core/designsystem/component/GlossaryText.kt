package com.abitusd.app.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import com.abitusd.app.core.designsystem.theme.AppTheme

/**
 * Text whose training terms (RPE, RIR, 1RM, deload...) open the "Aprende" glossary when tapped
 * (11f). Without navigation (for example before signing in) it is plain text.
 */
@Composable
fun GlossaryText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val open = LocalOpenGlossary.current
    if (open == null || TERMS.find(text) == null) {
        Text(text, modifier, color = color, style = style)
        return
    }
    val link =
        TextLinkStyles(
            SpanStyle(
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
            ),
        )
    val annotated =
        buildAnnotatedString {
            var last = 0
            TERMS.findAll(text).forEach { match ->
                append(text.substring(last, match.range.first))
                withLink(LinkAnnotation.Clickable(match.value, link) { open() }) { append(match.value) }
                last = match.range.last + 1
            }
            append(text.substring(last))
        }
    Text(annotated, modifier, color = color, style = style)
}

private val TERMS =
    Regex(
        "\\b(sRPE|RPE|RIR|e?1RM|EMOM|E2MOM|AMRAP|Tabata|[Dd]escargas?|[Dd]eloads?|[Mm]esociclos?|[Mm]esocycles?|" +
            "[Ss]uperseries?|[Ss]upersets?|[Ss]obrecarga progresiva|[Pp]rogressive overload)\\b",
    )
