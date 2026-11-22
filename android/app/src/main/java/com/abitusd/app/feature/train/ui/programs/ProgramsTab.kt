package com.abitusd.app.feature.train.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.motion.staggeredEnter
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.train.data.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProgramsViewModel
    @Inject
    constructor(
        repository: ProgramRepository,
    ) : ViewModel() {
        val programs =
            repository.programs().stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_MS),
                emptyList(),
            )

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

@Composable
fun ProgramsTab(
    onOpenProgram: (String?) -> Unit,
    viewModel: ProgramsViewModel = hiltViewModel(),
) {
    val programs by viewModel.programs.collectAsStateWithLifecycle()
    LazyColumn(
        contentPadding =
            PaddingValues(
                start = AppSpacing.screenMargin,
                end = AppSpacing.screenMargin,
                bottom = AppSpacing.xl,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        item { AppButton(stringResource(R.string.train_new_program), { onOpenProgram(null) }, icon = AppIcons.Add) }
        if (programs.isEmpty()) item { EmptyState(AppIcons.Calendar, stringResource(R.string.train_no_programs)) }
        itemsIndexed(programs, key = { _, p -> p.id }) { index, program ->
            ListItemCard(
                title = program.name,
                onClick = { onOpenProgram(program.id) },
                modifier = Modifier.staggeredEnter(index),
                icon = AppIcons.Calendar,
                meta = stringResource(R.string.program_summary, program.weeksCount),
            ) {
                if (program.isActive) InfoPill(stringResource(R.string.program_active), emphasized = true)
            }
        }
    }
}
