package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/**
 * Week templates (10f Fase 4b) are one-week programs: saving copies the week's workouts to a new
 * program, and applying it is activating that program from Programs.
 */
@HiltViewModel
class WeekTemplateViewModel
    @Inject
    constructor(
        private val calendar: CalendarRepository,
        private val programs: ProgramRepository,
    ) : ViewModel() {
        private val saved = MutableStateFlow(false)
        val done = saved.asStateFlow()

        fun save(
            day: LocalDate,
            name: String,
        ) {
            viewModelScope.launch {
                val monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val rows = calendar.between(monday, monday.plusDays(LAST_DAY)).first().filter { it.status != "skipped" }
                val program = ProgramRepository.newProgram(name, 1)
                val days =
                    rows.map {
                        val index = (LocalDate.parse(it.date).toEpochDay() - monday.toEpochDay()).toInt()
                        ProgramRepository.newDay(program.id, 0, index, it.routineId)
                    }
                programs.save(program, days.distinctBy { it.dayIndex })
                saved.value = true
            }
        }

        private companion object {
            const val LAST_DAY = 6L
        }
    }

@Composable
fun WeekTemplate(
    selected: LocalDate,
    viewModel: WeekTemplateViewModel = hiltViewModel(),
) {
    val done by viewModel.done.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val monday = selected.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val name = stringResource(R.string.template_week_name, monday.format(DateTimeFormatter.ofPattern("d MMM", locale)))
    AppButton(
        stringResource(R.string.template_week_save),
        { viewModel.save(selected, name) },
        style = AppButtonStyle.Tonal,
        icon = AppIcons.Duplicate,
    )
    if (done) StatusBanner(stringResource(R.string.template_week_saved), StatusKind.Success)
}
