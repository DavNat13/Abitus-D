package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.domain.IcsBuilder
import com.abitusd.app.feature.train.domain.IcsEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

internal fun statusOf(
    s: CalendarState,
    date: LocalDate,
): String? {
    val rows = s.scheduled.filter { it.date == date.toString() }
    return when {
        rows.isEmpty() -> null
        rows.any { it.status == "planned" } -> "planned"
        rows.all { it.status == CalendarViewModel.STATUS_SKIPPED } -> CalendarViewModel.STATUS_SKIPPED
        else -> CalendarViewModel.STATUS_DONE
    }
}

@Composable
internal fun DayCell(
    date: LocalDate,
    inMonth: Boolean,
    selected: Boolean,
    status: String?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    val background = if (selected) colors.primaryContainer else colors.background
    Box(
        modifier.aspectRatio(1f).background(background, AppShapes.field).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                date.dayOfMonth.toString(),
                color = if (inMonth) colors.textPrimary else colors.textSecondary,
                style = AppTheme.typography.body,
            )
            if (status != null) {
                val dot =
                    when (status) {
                        CalendarViewModel.STATUS_DONE -> colors.success
                        CalendarViewModel.STATUS_SKIPPED -> colors.textSecondary
                        else -> colors.primary
                    }
                Box(Modifier.padding(top = AppSpacing.xxs).background(dot, AppShapes.pill).padding(AppSpacing.xxs))
            }
        }
    }
}
