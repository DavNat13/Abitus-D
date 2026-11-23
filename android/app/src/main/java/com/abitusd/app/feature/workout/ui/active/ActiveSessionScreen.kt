package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.list.LibraryScreen
import kotlinx.coroutines.launch

/** Active session: one exercise per page (swipe), header with time and actions, rest bar. */
@Composable
fun ActiveSessionScreen(
    onBack: () -> Unit,
    onFinish: (String) -> Unit,
    viewModel: ActiveSessionViewModel = hiltViewModel(),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showMap by rememberSaveable { mutableStateOf(false) }
    var picking by rememberSaveable { mutableStateOf(false) }
    var plates by remember { mutableStateOf<Double?>(null) }
    if (picking) {
        LibraryScreen(onOpenExercise = {
            viewModel.addExercise(it)
            picking = false
        }, onCreateExercise = {}, showCreate = false)
        return
    }
    val session = s.session
    Box(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(Modifier.fillMaxSize()) {
            SessionHeader(session?.title.orEmpty(), onBack)
            if (!s.loading && s.blocks.isEmpty()) {
                EmptyState(
                    AppIcons.Train,
                    stringResource(
                        R.string.workout_no_exercises,
                    ),
                    actionLabel = stringResource(R.string.workout_add_exercise),
                    onAction = {
                        picking =
                            true
                    },
                )
            } else if (s.blocks.isNotEmpty()) {
                val pager = rememberPagerState(initialPage = s.index) { s.blocks.size }
                LaunchedEffect(pager) { snapshotFlow { pager.settledPage }.collect(viewModel::select) }
                LaunchedEffect(s.index) { if (pager.settledPage != s.index) pager.animateScrollToPage(s.index) }
                HorizontalPager(pager, Modifier.weight(1f), beyondViewportPageCount = 1) { page ->
                    val block = s.blocks[page]
                    ExercisePage(block, page, s.blocks.size, viewModel) {
                        plates =
                            block.sets.firstOrNull()?.loadKg ?: 0.0
                    }
                }
            }
            if (s.blocks.isEmpty()) Spacer(Modifier.weight(1f))
            s.restEndsAt?.let { endsAt ->
                RestBar(endsAt, s.restTotalS, {
                    scope.launch { viewModel.rest.addFifteen() }
                }, { scope.launch { viewModel.rest.skip() } })
            }
            SessionBottomBar(
                startedAt = session?.startedAt,
                canUndo = s.canUndo,
                canRedo = s.canRedo,
                onUndo = { viewModel.history(redo = false) },
                onRedo = { viewModel.history(redo = true) },
                onMap = { showMap = true },
                onFinish = { session?.let { onFinish(it.id) } },
            )
        }
    }
    if (showMap) {
        SessionMapSheet(
            blocks = s.blocks,
            onJump = {
                viewModel.select(it)
                showMap = false
            },
            onMove = viewModel::move,
            onAdd = {
                showMap = false
                picking = true
            },
            onDismiss = { showMap = false },
        )
    }
    plates?.let { PlateCalculatorSheet(it) { plates = null } }
}
