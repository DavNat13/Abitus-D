package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.ExerciseListRow
import com.abitusd.app.core.designsystem.component.AppChip
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.icon.DisciplineIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.domain.EquipmentProfile

@Composable
fun ExerciseItem(
    row: ExerciseListRow,
    state: LibraryState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val discipline =
        state.disciplines
            .firstOrNull { it.id == row.disciplineId }
            ?.name
            .orEmpty()
    ListItemCard(
        title = row.name,
        onClick = onClick,
        modifier = modifier,
        icon = DisciplineIcons.of(state.icons[row.disciplineId]),
        meta = stringResource(R.string.library_item_meta, discipline, row.difficulty),
    ) {
        if (row.isOwn) OwnTag()
    }
}

@Composable
private fun OwnTag() {
    val colors = AppTheme.colors
    Text(
        stringResource(R.string.library_own_tag),
        style = AppTheme.typography.caption,
        color = colors.onPrimaryContainer,
        modifier =
            Modifier
                .background(
                    colors.primaryContainer,
                    AppShapes.pill,
                ).padding(horizontal = AppSpacing.xs, vertical = AppSpacing.xxxs),
    )
}
