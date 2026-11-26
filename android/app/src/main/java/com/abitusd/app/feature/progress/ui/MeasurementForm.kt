package com.abitusd.app.feature.progress.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.progress.BodyMeasurementEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import java.time.LocalDate

/** Today's measurements: weight in kg and girths in cm on screen, stored in meters (SI). */
@Composable
fun MeasurementForm(onSave: (BodyMeasurementEntity) -> Unit) {
    var weight by rememberSaveable { mutableStateOf<Double?>(null) }
    var cm by rememberSaveable { mutableStateOf(List<Double?>(FIELDS.size) { null }) }
    SectionCard(stringResource(R.string.measure_today), icon = AppIcons.Weight) {
        NumberField(weight, stringResource(R.string.measure_weight), { weight = it.toDoubleOrNull() }, decimal = true)
        FIELDS.chunked(2).forEachIndexed { row, pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                pair.forEachIndexed { col, (label, _) ->
                    val i = row * 2 + col
                    NumberField(cm[i], stringResource(label), { v ->
                        cm = cm.toMutableList().also { it[i] = v.toDoubleOrNull() }
                    }, Modifier.weight(1f), decimal = true)
                }
            }
        }
        AppButton(stringResource(R.string.measure_save), {
            val base = BodyMeasurementEntity("", LocalDate.now().toString(), weight, updatedAt = 0)
            onSave(FIELDS.foldIndexed(base) { i, acc, field -> field.second(acc, cm[i]?.div(CM_PER_M)) })
            weight = null
            cm = List(FIELDS.size) { null }
        }, enabled = weight != null || cm.any { it != null }, icon = AppIcons.Check)
    }
}

private val FIELDS: List<Pair<Int, (BodyMeasurementEntity, Double?) -> BodyMeasurementEntity>> =
    listOf(
        R.string.measure_waist to { m, v -> m.copy(waistM = v) },
        R.string.measure_chest to { m, v -> m.copy(chestM = v) },
        R.string.measure_hip to { m, v -> m.copy(hipM = v) },
        R.string.measure_arm to { m, v -> m.copy(armM = v) },
        R.string.measure_thigh to { m, v -> m.copy(thighM = v) },
    )

private const val CM_PER_M = 100.0
