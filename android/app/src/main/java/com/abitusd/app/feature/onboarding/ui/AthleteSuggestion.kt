package com.abitusd.app.feature.onboarding.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.feature.onboarding.data.AthleteRecommendation
import com.abitusd.app.feature.train.domain.PlanTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AthleteSuggestionViewModel
    @Inject
    constructor(
        private val recommendations: AthleteRecommendation,
    ) : ViewModel() {
        var template by mutableStateOf<PlanTemplate?>(null)
            private set
        var busy by mutableStateOf(false)
            private set
        var message by mutableStateOf<Int?>(null)
            private set

        suspend fun suggest(profile: AthleteProfileEntity) {
            template = recommendations.suggest(profile)
        }

        fun install() {
            val selected = template
            if (selected == null || busy || message == R.string.athlete_template_added) return
            busy = true
            viewModelScope.launch {
                try {
                    message =
                        if (recommendations.install(
                                selected,
                            )
                        ) {
                            R.string.athlete_template_added
                        } else {
                            R.string.athlete_template_missing
                        }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    message = R.string.athlete_save_error
                } finally {
                    busy = false
                }
            }
        }
    }

@Composable
fun AthleteSuggestion(
    profile: AthleteProfileEntity,
    viewModel: AthleteSuggestionViewModel = hiltViewModel(),
) {
    LaunchedEffect(profile.goal, profile.daysPerWeek, profile.equipment, profile.healthLimited, profile.injuries) {
        viewModel.suggest(profile)
    }
    val template = viewModel.template
    Text(stringResource(R.string.athlete_template_title))
    if (template == null) {
        Text(stringResource(R.string.athlete_template_missing))
    } else {
        Text(stringResource(template.name))
        Text(stringResource(template.description))
        Text(stringResource(R.string.athlete_template_reason))
        if (viewModel.message != R.string.athlete_template_added) {
            AppButton(
                stringResource(R.string.athlete_template_add),
                viewModel::install,
                style = AppButtonStyle.Secondary,
                loading = viewModel.busy,
            )
        }
    }
    viewModel.message?.let { Text(stringResource(it)) }
}
