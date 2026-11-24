package com.abitusd.app.feature.workout.ui

import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.InfoEntry
import com.abitusd.app.core.designsystem.icon.AppIcons

/** Contents of the header info sheets of the workout screens (strings_info.xml). */
object WorkoutInfo {
    val session =
        listOf(
            InfoEntry(AppIcons.Steps, R.string.info_sets_t, R.string.info_sets_d),
            InfoEntry(AppIcons.Check, R.string.info_confirm_t, R.string.info_confirm_d),
            InfoEntry(AppIcons.Timer, R.string.info_rest_t, R.string.info_rest_d),
            InfoEntry(AppIcons.History, R.string.info_last_t, R.string.info_last_d),
            InfoEntry(AppIcons.ChevronRight, R.string.info_swipe_t, R.string.info_swipe_d),
            InfoEntry(AppIcons.Undo, R.string.info_undo_t, R.string.info_undo_d),
            InfoEntry(AppIcons.Steps, R.string.info_map_t, R.string.info_map_d),
            InfoEntry(AppIcons.Timer, R.string.info_time_t, R.string.info_time_d),
            InfoEntry(AppIcons.Finish, R.string.info_finish_t, R.string.info_finish_d),
            InfoEntry(AppIcons.Calculator, R.string.info_plates_t, R.string.info_plates_d),
        )

    val preview =
        listOf(
            InfoEntry(AppIcons.Steps, R.string.routine_exercises, R.string.info_preview_list_d),
            InfoEntry(AppIcons.Progression, R.string.plan_title, R.string.plan_progression_applied),
            InfoEntry(AppIcons.Alternative, R.string.plan_travel, R.string.info_travel_d),
            InfoEntry(AppIcons.Rest, R.string.info_readiness_t, R.string.info_readiness_d),
            InfoEntry(AppIcons.Start, R.string.info_start_t, R.string.info_start_d),
        )

    val summary =
        listOf(
            InfoEntry(AppIcons.Timer, R.string.info_metrics_t, R.string.info_metrics_d),
            InfoEntry(AppIcons.Progression, R.string.info_records_t, R.string.info_records_d),
            InfoEntry(AppIcons.Check, R.string.info_save_t, R.string.info_save_d),
        )

    val history = listOf(InfoEntry(AppIcons.History, R.string.info_screen_history, R.string.info_history_d))

    val train =
        listOf(
            InfoEntry(AppIcons.Start, R.string.info_today_t, R.string.info_today_d),
            InfoEntry(AppIcons.Train, R.string.info_free_t, R.string.info_free_d),
            InfoEntry(AppIcons.Reschedule, R.string.info_missed_t, R.string.info_missed_d),
            InfoEntry(AppIcons.History, R.string.info_screen_history, R.string.info_history_d),
            InfoEntry(AppIcons.Calendar, R.string.info_tabs_t, R.string.info_tabs_d),
        )

    val library =
        listOf(
            InfoEntry(AppIcons.Search, R.string.info_search_t, R.string.info_search_d),
            InfoEntry(AppIcons.FilterList, R.string.info_filters_t, R.string.info_filters_d),
            InfoEntry(AppIcons.Train, R.string.info_card_t, R.string.info_card_d),
            InfoEntry(AppIcons.Add, R.string.info_create_t, R.string.info_create_d),
        )

    val home =
        listOf(
            InfoEntry(AppIcons.Today, R.string.info_today_t, R.string.info_home_today_d),
            InfoEntry(AppIcons.Calendar, R.string.info_next_t, R.string.info_next_d),
            InfoEntry(AppIcons.Profile, R.string.info_profile_t, R.string.info_profile_d),
        )
}
