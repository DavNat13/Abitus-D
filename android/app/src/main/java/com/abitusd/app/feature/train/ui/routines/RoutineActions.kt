package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.list.LibraryScreen

@Composable
internal fun RoutineActions(
    archived: Boolean,
    viewModel: RoutineEditorViewModel,
    onDelete: () -> Unit,
) {
    val copySuffix = stringResource(R.string.routine_copy_suffix)
    AppButton(stringResource(R.string.routine_schedule), {
        viewModel.run(RoutineAction.SCHEDULE_TODAY)
    }, style = AppButtonStyle.Secondary, icon = AppIcons.Today)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        AppButton(
            stringResource(R.string.routine_duplicate),
            { viewModel.run(RoutineAction.DUPLICATE, copySuffix) },
            Modifier.weight(1f),
            AppButtonStyle.Tonal,
            icon = AppIcons.Duplicate,
        )
        AppButton(
            stringResource(if (archived) R.string.routine_unarchive else R.string.routine_archive),
            { viewModel.run(RoutineAction.ARCHIVE) },
            Modifier.weight(1f),
            AppButtonStyle.Tonal,
            icon = if (archived) AppIcons.Unarchive else AppIcons.Archive,
        )
    }
    AppButton(
        stringResource(R.string.routine_delete),
        onDelete,
        style = AppButtonStyle.Destructive,
        icon = AppIcons.Delete,
    )
}
