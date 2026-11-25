package com.abitusd.app.feature.onboarding.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.theme.AppTheme

@Composable
fun AthleteHealth(
    p: AthleteProfileEntity,
    change: (AthleteProfileEntity) -> Unit,
) {
    val context = LocalContext.current
    var unavailable by remember { mutableStateOf(false) }
    val options =
        listOf(
            ChoiceOption(true, stringResource(R.string.athlete_yes)),
            ChoiceOption(false, stringResource(R.string.athlete_no)),
        )
    Text(stringResource(R.string.athlete_health_consent), style = AppTheme.typography.body)
    AppDropdown(stringResource(R.string.athlete_consent_answer), options, p.healthConsent, {
        change(
            if (it ==
                true
            ) {
                p.copy(healthConsent = true)
            } else {
                p.copy(healthConsent = false, healthLimited = null, injuries = "")
            },
        )
    })
    if (p.healthConsent) {
        Text(stringResource(R.string.athlete_health_question), style = AppTheme.typography.body)
        AppDropdown(stringResource(R.string.athlete_answer), options, p.healthLimited, {
            change(p.copy(healthLimited = it))
        }, noneLabel = stringResource(R.string.athlete_not_set))
        if (p.healthLimited == true) {
            Text(stringResource(R.string.athlete_health_notice), style = AppTheme.typography.body)
            AppButton(stringResource(R.string.athlete_health_link), {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PAR_Q_URL)))
                } catch (_: ActivityNotFoundException) {
                    unavailable = true
                }
            }, style = AppButtonStyle.Secondary)
            if (unavailable) Text(stringResource(R.string.athlete_browser_unavailable))
        }
        AppTextField(
            p.injuries,
            { change(p.copy(injuries = it.take(MAX_INJURIES))) },
            stringResource(R.string.athlete_injuries),
            supportingText = stringResource(R.string.athlete_injuries_help),
        )
    }
}

@Composable
fun AthleteBaseline(
    p: AthleteProfileEntity,
    change: (AthleteProfileEntity) -> Unit,
) {
    AthleteSuggestion(p)
    Text(stringResource(R.string.athlete_tests_note), style = AppTheme.typography.body)
    NumberField(p.pushUps, stringResource(R.string.athlete_push_ups), { change(p.copy(pushUps = it.toIntOrNull())) })
    NumberField(p.pullUps, stringResource(R.string.athlete_pull_ups), { change(p.copy(pullUps = it.toIntOrNull())) })
    NumberField(p.plankDurationS, stringResource(R.string.athlete_plank), {
        change(p.copy(plankDurationS = it.toIntOrNull()))
    })
}

private const val PAR_Q_URL = "https://eparmedx.com/print-versions-of-par-q/"
private const val MAX_INJURIES = 1000
