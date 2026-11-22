package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
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
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.motion.staggeredEnter
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
        contentPadding =
            PaddingValues(
                start = AppSpacing.screenMargin,
                end = AppSpacing.screenMargin,
                bottom = AppSpacing.xl,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                AppButton(
                    stringResource(R.string.train_new_routine),
                    { onOpenRoutine(null) },
                    Modifier.weight(1f),
                    icon = AppIcons.Add,
                )
                AppButton(
                    stringResource(R.string.train_templates),
                    { showTemplates = true },
                    Modifier.weight(1f),
                    AppButtonStyle.Tonal,
                )
            }
        }
        message?.let { item { StatusBanner(stringResource(it), StatusKind.Success) } }
        if (routines.isEmpty()) item { EmptyState(AppIcons.Train, stringResource(R.string.train_no_routines)) }
        groups.forEach { (folder, list) ->
            item { SectionHeader(folder) }
            itemsIndexed(list, key = { _, r -> r.id }) { index, routine ->
                ListItemCard(
                    title = routine.name,
                    onClick = { onOpenRoutine(routine.id) },
                    modifier = Modifier.staggeredEnter(index),
                    icon = if (routine.archived) AppIcons.Archive else AppIcons.Train,
                    meta =
                        routine.notes
                            .lineSequence()
                            .firstOrNull()
                            ?.takeIf { it.isNotBlank() },
                    muted = routine.archived,
                )
            }
        }
    }
    if (showTemplates) {
        AppBottomSheet(stringResource(R.string.train_templates), { showTemplates = false }) {
            TemplateCatalog.all.forEach { template ->
                ListItemCard(
                    title = stringResource(template.name),
                    meta = stringResource(template.description),
                    icon = AppIcons.Calendar,
                    onClick = {
                        showTemplates = false
                        viewModel.install(template)
                    },
                )
            }
        }
    }
}

/** Secondary hint line used by the train tabs. */
@Composable
fun Hint(text: String) {
    Text(
        text,
        style = AppTheme.typography.body,
        color = AppTheme.colors.textSecondary,
        modifier = Modifier.padding(AppSpacing.xs),
    )
}
