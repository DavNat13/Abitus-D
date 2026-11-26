package com.abitusd.app.feature.clock.ui

import android.os.SystemClock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import kotlinx.coroutines.delay

/** Isometric holds and skill practice with the best time per exercise (11b, 11c). */
@Composable
fun HoldCard(vm: ClockExtrasViewModel = hiltViewModel()) {
    var name by rememberSaveable { mutableStateOf(ClockPending.label.orEmpty()) }
    var startedAt by rememberSaveable { mutableLongStateOf(0L) }
    var shown by remember { mutableLongStateOf(0L) }
    var holds by remember { mutableStateOf(vm.records.holds()) }
    var record by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(startedAt) {
        while (startedAt > 0) {
            shown = SystemClock.elapsedRealtime() - startedAt
            delay(TICK_MS)
        }
    }
    SectionCard(stringResource(R.string.hold_title), icon = AppIcons.Timer) {
        Text(
            stringResource(R.string.hold_note),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
        AppTextField(name, { name = it.take(MAX_NAME) }, stringResource(R.string.hold_name))
        if (startedAt >
            0
        ) {
            Text(
                ClockLabels.time(shown, tenths = true),
                style = AppTheme.typography.displayNumeric.copy(fontSize = BIG.sp),
            )
        }
        record?.let {
            Text(
                stringResource(R.string.hold_new_record, ClockLabels.time(it, tenths = true)),
                color = AppTheme.colors.primary,
            )
        }
        holds[name.trim()]?.takeIf { it.isNotEmpty() }?.let { list ->
            val t = { ms: Long -> ClockLabels.time(ms, tenths = true) }
            Text(
                stringResource(
                    R.string.hold_best,
                    t(list.max()),
                    list.take(LAST).joinToString(", ") {
                        t(it)
                    },
                ),
                style = AppTheme.typography.body,
            )
        }
        AppButton(
            stringResource(if (startedAt > 0) R.string.hold_stop else R.string.hold_start),
            {
                if (startedAt > 0) {
                    val ms = SystemClock.elapsedRealtime() - startedAt
                    val key = name.trim()
                    val best = holds[key].orEmpty().maxOrNull() ?: 0L
                    vm.records.addHold(key, ms)
                    holds = vm.records.holds()
                    record = ms.takeIf { it > best }
                    startedAt = 0
                } else {
                    record = null
                    startedAt = SystemClock.elapsedRealtime()
                }
            },
            enabled = name.isNotBlank(),
            style =
                if (startedAt >
                    0
                ) {
                    AppButtonStyle.Destructive
                } else {
                    AppButtonStyle.Tonal
                },
            icon = AppIcons.Start,
        )
    }
}

private const val TICK_MS = 100L
private const val BIG = 40
private const val LAST = 3
private const val MAX_NAME = 60
