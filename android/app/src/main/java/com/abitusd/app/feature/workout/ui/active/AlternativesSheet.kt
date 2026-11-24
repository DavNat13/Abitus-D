package com.abitusd.app.feature.workout.ui.active

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.train.ui.routines.Hint
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** One related exercise of the catalog: id, name and relation type (alternative, progression, regression). */
data class RelatedOption(
    val id: String,
    val name: String,
    val type: String,
)

@HiltViewModel(assistedFactory = AlternativesViewModel.Factory::class)
class AlternativesViewModel
    @AssistedInject
    constructor(
        @Assisted exerciseId: String,
        library: LibraryDao,
        names: ExerciseNames,
    ) : ViewModel() {
        val options =
            library
                .relations(exerciseId)
                .map { rows ->
                    val all = names.all()
                    rows
                        .mapNotNull { r ->
                            all[r.relatedId]?.let { RelatedOption(r.relatedId, it, r.type) }
                        }.sortedBy { it.type }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), emptyList())

        @AssistedFactory
        interface Factory {
            fun create(exerciseId: String): AlternativesViewModel
        }

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

/** Swap the exercise: alternatives, easier (regression) and harder (progression) versions. */
@Composable
fun AlternativesSheet(
    exerciseId: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: AlternativesViewModel =
        hiltViewModel<AlternativesViewModel, AlternativesViewModel.Factory>(
            key = "alt-$exerciseId",
            creationCallback = { it.create(exerciseId) },
        ),
) {
    val options by viewModel.options.collectAsStateWithLifecycle()
    AppBottomSheet(stringResource(R.string.tools_swap), onDismiss) {
        if (options.isEmpty()) Hint(stringResource(R.string.tools_no_alternatives))
        options.forEach { option ->
            val (icon, label) =
                when (option.type) {
                    "progression" -> AppIcons.Progression to R.string.detail_progressions
                    "regression" -> AppIcons.Regression to R.string.detail_regressions
                    else -> AppIcons.Alternative to R.string.detail_alternatives
                }
            ListItemCard(option.name, { onPick(option.id) }, icon = icon, meta = stringResource(label))
        }
    }
}
