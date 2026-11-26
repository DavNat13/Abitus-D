package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.data.MultiTimers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MultiTimerViewModel
    @Inject
    constructor(
        val timers: MultiTimers,
    ) : ViewModel()

/** Multi timer (11e): several named countdowns at once, each with its own alarm. */
@Composable
fun MultiTimerCard(vm: MultiTimerViewModel = hiltViewModel()) {
    val list by vm.timers.all.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableIntStateOf(DEFAULT_MIN) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(list.isNotEmpty()) {
        while (list.isNotEmpty()) {
            now = System.currentTimeMillis()
            delay(TICK_MS)
        }
    }
    SectionCard(stringResource(R.string.multi_title), icon = AppIcons.Timer) {
        if (list.isEmpty()) {
            Text(
                stringResource(R.string.multi_empty),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSecondary,
            )
        }
        list.forEach { t ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Text(
                    "${t.name}: ${ClockLabels.time((t.endsAt - now).coerceAtLeast(0))}",
                    Modifier.weight(1f),
                    style = AppTheme.typography.titleMedium,
                )
                AppButton(stringResource(R.string.multi_remove), {
                    vm.timers.remove(t.id)
                }, style = AppButtonStyle.Text, icon = AppIcons.Remove)
            }
        }
        AppTextField(name, { name = it.take(MAX_NAME) }, stringResource(R.string.multi_name))
        Stepper(
            stringResource(R.string.multi_minutes),
            minutes.toString(),
            stringResource(R.string.clock_less),
            stringResource(R.string.clock_more),
            { minutes = maxOf(1, minutes - 1) },
            { minutes = minOf(MAX_MIN, minutes + 1) },
        )
        AppButton(stringResource(R.string.multi_add), {
            vm.timers.add(name.trim(), minutes * SECONDS)
            name = ""
        }, enabled = name.isNotBlank(), style = AppButtonStyle.Tonal, icon = AppIcons.Add)
    }
}

private const val DEFAULT_MIN = 5
private const val MAX_MIN = 600
private const val SECONDS = 60
private const val TICK_MS = 500L
private const val MAX_NAME = 40
