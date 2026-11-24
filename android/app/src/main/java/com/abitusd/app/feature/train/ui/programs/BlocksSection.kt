package com.abitusd.app.feature.train.ui.programs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ProgramBlockEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.MenuAction
import com.abitusd.app.core.designsystem.component.OverflowMenu
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.data.BlockRepository

/**
 * Periodization of a saved program (10f Fase 4b): its blocks as cards, a week timeline and a
 * shortcut for a 4-week mesocycle. [onWeeks] keeps the program length equal to the blocks.
 */
@Composable
fun BlocksSection(
    programId: String,
    onWeeks: (Int) -> Unit,
    viewModel: ProgramBlocksViewModel =
        hiltViewModel<ProgramBlocksViewModel, ProgramBlocksViewModel.Factory>(
            key = "blocks-$programId",
            creationCallback = { it.create(programId) },
        ),
) {
    val blocks by viewModel.blocks.collectAsStateWithLifecycle()
    var editing by rememberSaveable { mutableStateOf<String?>(null) }
    val total = blocks.sumOf { it.weeks }
    LaunchedEffect(total) { if (total > 0) onWeeks(total) }
    SectionCard(stringResource(R.string.blocks_title), icon = AppIcons.Progression) {
        if (blocks.isEmpty()) {
            Text(
                stringResource(R.string.blocks_empty),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSecondary,
            )
            AppButton(
                stringResource(R.string.blocks_mesocycle),
                viewModel::addMesocycle,
                style = AppButtonStyle.Tonal,
                icon = AppIcons.Progression,
            )
        } else {
            BlockTimeline(
                blocks.map(BlockRepository::spec),
                null,
                pluralStringResource(R.plurals.blocks_total_weeks, total, total),
            )
            blocks.forEachIndexed { index, block ->
                BlockCard(block, index, blocks.lastIndex, viewModel) {
                    editing =
                        block.id
                }
            }
        }
        AppButton(stringResource(R.string.blocks_add), viewModel::add, style = AppButtonStyle.Text, icon = AppIcons.Add)
    }
    blocks.firstOrNull { it.id == editing }?.let { block ->
        BlockSheet(block, onChange = viewModel::update, onDismiss = { editing = null })
    }
}

@Composable
private fun BlockCard(
    block: ProgramBlockEntity,
    index: Int,
    last: Int,
    viewModel: ProgramBlocksViewModel,
    onOpen: () -> Unit,
) {
    val title = block.name.ifBlank { stringResource(phaseLabel(block.phase)) }
    val meta =
        pluralStringResource(R.plurals.blocks_weeks, block.weeks, block.weeks) + " - " +
            stringResource(progressionLabel(block.progression))
    ListItemCard(title, onOpen, icon = phaseIcon(block.phase), meta = meta) {
        val actions =
            listOfNotNull(
                MenuAction(stringResource(R.string.blocks_move_up), AppIcons.MoveUp) { viewModel.move(block.id, -1) }
                    .takeIf { index > 0 },
                MenuAction(stringResource(R.string.blocks_move_down), AppIcons.MoveDown) { viewModel.move(block.id, 1) }
                    .takeIf { index < last },
                MenuAction(stringResource(R.string.blocks_remove), AppIcons.Delete, destructive = true) {
                    viewModel.remove(block.id)
                },
            )
        OverflowMenu(stringResource(R.string.blocks_options, title), actions)
    }
}
