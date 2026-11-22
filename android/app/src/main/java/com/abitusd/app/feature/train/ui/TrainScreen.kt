package com.abitusd.app.feature.train.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.ScreenHeader
import com.abitusd.app.core.designsystem.component.SegmentedTabs
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.ui.calendar.CalendarTab
import com.abitusd.app.feature.train.ui.programs.ProgramsTab
import com.abitusd.app.feature.train.ui.routines.RoutinesTab

private val tabs = listOf(R.string.train_tab_routines, R.string.train_tab_programs, R.string.train_tab_calendar)

/** Train section: routines, programs and the planning calendar. */
@Composable
fun TrainScreen(
    onOpenRoutine: (String?) -> Unit,
    onOpenProgram: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier.fillMaxSize().background(AppTheme.colors.background).statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        ScreenHeader(stringResource(R.string.nav_train))
        SegmentedTabs(
            labels = tabs.map { stringResource(it) },
            selected = selected,
            onSelect = { selected = it },
            modifier = Modifier.padding(horizontal = AppSpacing.screenMargin),
        )
        when (selected) {
            0 -> RoutinesTab(onOpenRoutine)
            1 -> ProgramsTab(onOpenProgram)
            else -> CalendarTab()
        }
    }
}
