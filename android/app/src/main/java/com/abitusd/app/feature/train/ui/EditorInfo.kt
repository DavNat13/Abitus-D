package com.abitusd.app.feature.train.ui

import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.InfoEntry
import com.abitusd.app.core.designsystem.icon.AppIcons

/** Info sheets of the editors, the exercise detail and the profile (strings_info.xml). */
object EditorInfo {
    val routine =
        listOf(
            InfoEntry(AppIcons.Notes, R.string.routine_details, R.string.info_routine_data_d),
            InfoEntry(AppIcons.Steps, R.string.routine_exercises, R.string.info_routine_ex_d),
            InfoEntry(AppIcons.More, R.string.common_more_actions, R.string.info_routine_menu_d),
            InfoEntry(AppIcons.Muscles, R.string.routine_volume, R.string.info_volume_d),
            InfoEntry(AppIcons.Today, R.string.routine_schedule, R.string.info_routine_actions_d),
        )

    val program =
        listOf(
            InfoEntry(AppIcons.Notes, R.string.program_name, R.string.info_program_data_d),
            InfoEntry(AppIcons.Progression, R.string.blocks_title, R.string.info_blocks_d),
            InfoEntry(AppIcons.Train, R.string.blocks_phase, R.string.info_phase_d),
            InfoEntry(AppIcons.Progression, R.string.blocks_progression, R.string.info_progression_d),
            InfoEntry(AppIcons.Calendar, R.string.info_program_week_t, R.string.info_program_week_d),
            InfoEntry(AppIcons.ActiveRest, R.string.program_active_rest_day, R.string.info_program_rest_d),
            InfoEntry(AppIcons.Today, R.string.program_activate, R.string.info_program_activate_d),
        )

    val detail =
        listOf(
            InfoEntry(AppIcons.Info, R.string.info_detail_tags_t, R.string.info_detail_tags_d),
            InfoEntry(AppIcons.Favorite, R.string.info_favorites_t, R.string.info_detail_favorite_d),
            InfoEntry(AppIcons.Muscles, R.string.detail_muscles_equipment, R.string.info_detail_muscles_d),
            InfoEntry(AppIcons.Steps, R.string.detail_execution, R.string.info_detail_steps_d),
            InfoEntry(AppIcons.Tip, R.string.detail_cues, R.string.info_detail_cues_d),
            InfoEntry(AppIcons.Alternative, R.string.detail_alternatives, R.string.info_detail_related_d),
        )

    val ownExercise =
        listOf(
            InfoEntry(AppIcons.Notes, R.string.form_section_basics, R.string.info_form_basics_d),
            InfoEntry(AppIcons.Train, R.string.form_section_type, R.string.info_form_type_d),
            InfoEntry(AppIcons.Muscles, R.string.detail_muscles_equipment, R.string.info_form_muscles_d),
        )

    val profile =
        listOf(
            InfoEntry(AppIcons.Profile, R.string.info_athlete_t, R.string.info_athlete_d),
            InfoEntry(AppIcons.Sync, R.string.info_sync_t, R.string.info_sync_d),
            InfoEntry(AppIcons.Library, R.string.info_learn_t, R.string.info_learn_d),
            InfoEntry(AppIcons.Filter, R.string.profile_preferences, R.string.info_profile_prefs_d),
            InfoEntry(AppIcons.Info, R.string.profile_legal, R.string.info_profile_legal_d),
            InfoEntry(AppIcons.Profile, R.string.profile_account, R.string.info_profile_account_d),
        )

    val athlete =
        listOf(
            InfoEntry(AppIcons.Profile, R.string.info_athlete_steps_t, R.string.info_athlete_steps_d),
            InfoEntry(AppIcons.Safety, R.string.info_athlete_health_t, R.string.info_athlete_health_d),
            InfoEntry(AppIcons.Train, R.string.info_athlete_plan_t, R.string.info_athlete_plan_d),
        )

    val clock =
        listOf(
            InfoEntry(AppIcons.Timer, R.string.info_clock_modes_t, R.string.info_clock_modes_d),
            InfoEntry(AppIcons.Pause, R.string.info_clock_background_t, R.string.info_clock_background_d),
            InfoEntry(AppIcons.Start, R.string.info_clock_signals_t, R.string.info_clock_signals_d),
            InfoEntry(AppIcons.Lap, R.string.info_clock_tools_t, R.string.info_clock_tools_d),
            InfoEntry(AppIcons.Share, R.string.info_clock_share_t, R.string.info_clock_share_d),
        )

    val glossary = listOf(InfoEntry(AppIcons.Library, R.string.info_glossary_t, R.string.info_glossary_d))
}
