package com.abitusd.app.feature.clock.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockPhase
import com.abitusd.app.feature.clock.domain.ClockPreset
import com.abitusd.app.feature.clock.domain.ClockSpec
import com.abitusd.app.feature.clock.domain.PhaseKind

/**
 * Guided sequences with named steps (11b, 11c): general warm-up of about 5 minutes, cool-down,
 * mobility and contrast heat and cold. Steps are common, well known exercises; names are spoken.
 */
@Composable
fun guidedPresets(): List<ClockPreset> =
    GUIDED.map { (key, steps) ->
        val phases = steps.map { (label, seconds, kind) -> ClockPhase(kind, seconds, label = stringResource(label)) }
        ClockPreset(key, ClockMode.CUSTOM, ClockSpec(phases, if (key == "contrast") CONTRAST_ROUNDS else 1))
    }

private data class Step(
    @param:StringRes val label: Int,
    val seconds: Int,
    val kind: String = PhaseKind.WORK,
)

private const val CONTRAST_ROUNDS = 3

@Suppress("MagicNumber")
private val GUIDED =
    listOf(
        "warmup_general" to
            listOf(
                Step(R.string.step_jumping_jacks, 45),
                Step(R.string.step_arm_circles, 30),
                Step(R.string.step_bodyweight_squats, 45),
                Step(R.string.step_hip_circles, 30),
                Step(R.string.step_inchworm, 45),
                Step(R.string.step_leg_swings, 45),
                Step(R.string.step_high_knees, 30),
            ),
        "cooldown" to
            listOf(
                Step(R.string.step_easy_walk, 120, PhaseKind.REST),
                Step(R.string.step_quad_stretch, 60, PhaseKind.REST),
                Step(R.string.step_hamstring_stretch, 60, PhaseKind.REST),
                Step(R.string.step_childs_pose, 45, PhaseKind.REST),
                Step(R.string.step_slow_breathing, 60, PhaseKind.EXHALE),
            ),
        "mobility_routine" to
            listOf(
                Step(R.string.step_cat_cow, 45),
                Step(R.string.step_worlds_greatest, 60),
                Step(R.string.step_hip_90_90, 60),
                Step(R.string.step_thoracic_rotation, 45),
                Step(R.string.step_ankle_rocks, 45),
            ),
        "contrast" to
            listOf(
                Step(R.string.step_heat, 600),
                Step(R.string.step_cold, 60, PhaseKind.REST),
            ),
    )
