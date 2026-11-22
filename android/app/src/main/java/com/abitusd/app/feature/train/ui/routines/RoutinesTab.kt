package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.domain.TemplateCatalog

@Composable
fun RoutinesTab(
    onOpenRoutine: (String?) -> Unit,
    viewModel: RoutinesViewModel = hiltViewModel(),
) {
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    var showTemplates by rememberSaveable { mutableStateOf(false) }
    val noFolder = stringResource(R.string.train_no_folder)
    val archivedLabel = stringResource(R.string.train_archived)
    val groups = routines.groupBy { if (it.archived) archivedLabel else it.folder.ifBlank { noFolder } }
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenMargin),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        item { AppButton(stringResource(R.string.train_new_routine), { onOpenRoutine(null) }) }
        item {
            AppButton(
                stringResource(R.string.train_templates),
                { showTemplates = true },
                style = AppButtonStyle.Secondary,
            )
        }
        message?.let { item { StatusBanner(stringResource(it), StatusKind.Success) } }
        if (routines.isEmpty()) item { Hint(stringResource(R.string.train_no_routines)) }
        groups.forEach { (folder, list) ->
            item { Text(folder, style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary) }
            items(list, key = { it.id }) { RoutineCard(it, onClick = { onOpenRoutine(it.id) }) }
        }
    }
    if (showTemplates) {
        AlertDialog(
            onDismissRequest = { showTemplates = false },
            title = { Text(stringResource(R.string.train_templates)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    TemplateCatalog.all.forEach { template ->
                        TextButton(onClick = {
                            showTemplates = false
                            viewModel.install(template)
                        }) {
                            Column {
                                Text(stringResource(template.name), style = AppTheme.typography.label)
                                Text(stringResource(template.description), style = AppTheme.typography.caption)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showTemplates = false },
                ) { Text(stringResource(R.string.detail_cancel)) }
            },
        )
    }
}

@Composable
private fun RoutineCard(
    routine: RoutineEntity,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        border = BorderStroke(AppSizes.borderThin, AppTheme.colors.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = routine.name,
            style = AppTheme.typography.titleMedium,
            color = if (routine.archived) AppTheme.colors.textSecondary else AppTheme.colors.textPrimary,
            modifier = Modifier.padding(AppSpacing.md),
        )
    }
}

@Composable
fun Hint(text: String) {
    Text(
        text,
        style = AppTheme.typography.body,
        color = AppTheme.colors.textSecondary,
        modifier = Modifier.padding(AppSpacing.md),
    )
}
