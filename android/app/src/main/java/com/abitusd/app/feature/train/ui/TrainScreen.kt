package com.abitusd.app.feature.train.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
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
    Column(modifier) {
        PrimaryTabRow(selectedTabIndex = selected, containerColor = AppTheme.colors.surface) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = index == selected,
                    onClick = { selected = index },
                    text = { Text(stringResource(label)) },
                )
            }
        }
        Spacer(Modifier.height(AppSpacing.md))
        when (selected) {
            0 -> RoutinesTab(onOpenRoutine)
            1 -> ProgramsTab(onOpenProgram)
            else -> CalendarTab()
        }
    }
}
