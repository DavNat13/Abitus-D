package com.abitusd.app.feature.workout.ui.preview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.ui.routines.Hint
import com.abitusd.app.feature.workout.ui.WorkoutInfo

/** Before the session: what it holds, about how long, an optional check-in and Start (10f). */
@Composable
fun PreSessionScreen(
    routineId: String?,
    scheduledWorkoutId: String?,
    onBack: () -> Unit,
    onStarted: () -> Unit,
    viewModel: PreSessionViewModel =
        hiltViewModel<PreSessionViewModel, PreSessionViewModel.Factory>(
            key = "pre-$routineId-$scheduledWorkoutId",
            creationCallback = { it.create(routineId, scheduledWorkoutId) },
        ),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val freeTitle = stringResource(R.string.workout_free)
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.start(freeTitle) }
    LaunchedEffect(viewModel) { viewModel.started.collect { onStarted() } }
    val scale = (1..SCALE_MAX).map { ChoiceOption(it, stringResource(R.string.workout_scale, it)) }
    FormScreen(
        title = s.title ?: freeTitle,
        onBack = onBack,
        info = { InfoButton(R.string.info_screen_preview, WorkoutInfo.preview) },
    ) {
        SectionCard(stringResource(R.string.routine_exercises), icon = AppIcons.Steps) {
            if (s.exercises.isEmpty()) Hint(stringResource(R.string.workout_no_exercises))
            s.exercises.forEachIndexed { index, (name, sets) ->
                if (index > 0) HorizontalDivider(color = AppTheme.colors.outline)
                Text(
                    stringResource(R.string.workout_exercise_sets, name, sets),
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textPrimary,
                )
            }
            if (s.exercises.isNotEmpty()) {
                InfoPill(
                    stringResource(R.string.workout_summary_line, s.exercises.size, s.minutes),
                    emphasized = true,
                )
            }
        }
        SectionCard(stringResource(R.string.workout_readiness), icon = AppIcons.Rest) {
            val none = stringResource(R.string.workout_not_set)
            AppDropdown(
                stringResource(R.string.workout_sleep),
                scale,
                s.readiness.sleep,
                viewModel::setSleep,
                noneLabel = none,
            )
            AppDropdown(
                stringResource(R.string.workout_energy),
                scale,
                s.readiness.energy,
                viewModel::setEnergy,
                noneLabel = none,
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !granted(context)) {
            Text(
                stringResource(R.string.notifications_rationale),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        AppButton(stringResource(R.string.workout_start), {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !granted(context)) {
                permission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.start(freeTitle)
            }
        }, loading = s.starting, icon = AppIcons.Start)
    }
    s.previousTitle?.let { title ->
        PreviousSessionSheet(title, {
            viewModel.resolvePrevious(it, freeTitle)
        }, viewModel::closePreviousSheet)
    }
}

private fun granted(context: android.content.Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

private const val SCALE_MAX = 5
