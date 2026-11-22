package com.abitusd.app.feature.library.domain

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.abitusd.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Equipment available at a place; [allowed] null means every exercise is shown. */
enum class EquipmentProfile(
    @param:StringRes val label: Int,
    val allowed: Set<String>?,
) {
    ALL(R.string.equipment_profile_all, null),
    GYM(R.string.equipment_profile_gym, setOf("pool", "bicycle", "kickboard", "pull_buoy", "fins")),
    HOME(
        R.string.equipment_profile_home,
        setOf(
            "bodyweight",
            "dumbbell",
            "kettlebell",
            "bands",
            "medicine_ball",
            "stability_ball",
            "foam_roller",
            "pull_up_bar",
            "jump_rope",
            "suspension_trainer",
            "box",
            "other",
        ),
    ),
    PARK(
        R.string.equipment_profile_park,
        setOf("bodyweight", "pull_up_bar", "dip_bars", "rings", "jump_rope", "bands", "box", "other"),
    ),
    ;

    /** GYM lists what it lacks; the other profiles list what they have. */
    val excludes: Boolean get() = this == GYM
}

/** Remembers the chosen profile on this device. */
@Singleton
class EquipmentProfileStore
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("abitusd_preferences", Context.MODE_PRIVATE)
        private val mutable =
            MutableStateFlow(
                EquipmentProfile.entries.firstOrNull { it.name == prefs.getString(KEY, null) } ?: EquipmentProfile.ALL,
            )
        val profile: StateFlow<EquipmentProfile> = mutable.asStateFlow()

        fun set(value: EquipmentProfile) {
            prefs.edit { putString(KEY, value.name) }
            mutable.value = value
        }

        private companion object {
            const val KEY = "equipment_profile"
        }
    }
