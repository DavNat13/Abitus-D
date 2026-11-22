package com.abitusd.app.feature.train.ui.programs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ProgramEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.data.ProgramRepository
import com.abitusd.app.feature.train.ui.routines.Hint
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenMargin),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        item { AppButton(stringResource(R.string.train_new_program), { onOpenProgram(null) }) }
        if (programs.isEmpty()) item { Hint(stringResource(R.string.train_no_programs)) }
        items(programs, key = { it.id }) { ProgramCard(it) { onOpenProgram(it.id) } }
    }
}

@Composable
private fun ProgramCard(
    program: ProgramEntity,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        border = BorderStroke(AppSizes.borderThin, AppTheme.colors.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(AppSpacing.md)) {
            Text(program.name, style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
            Text(
                stringResource(R.string.program_summary, program.weeksCount),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
            if (program.isActive) {
                Text(
                    stringResource(R.string.program_active),
                    style = AppTheme.typography.caption,
                    color = AppTheme.colors.primary,
                )
            }
        }
    }
}
