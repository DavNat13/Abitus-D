package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

private const val SCRIM_ALPHA = 0.4f

/** Bottom sheet of 05c: top radius 24, drag handle, ink scrim at 40 percent, scrollable content. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = AppTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = AppShapes.sheet,
        containerColor = colors.surface,
        contentColor = colors.textPrimary,
        scrimColor = colors.ink.copy(alpha = SCRIM_ALPHA),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = AppSpacing.sm)
                    .size(AppSizes.sheetHandleWidth, AppSizes.sheetHandle)
                    .background(colors.outlineStrong, AppShapes.pill),
            )
        },
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppSpacing.screenMargin)
                .padding(top = AppSpacing.md, bottom = AppSpacing.xl)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            Text(
                title,
                style = AppTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier =
                    Modifier
                        .semantics {
                            heading()
                        },
            )
            content()
        }
    }
}
