package com.abitusd.app.feature.train.domain

import androidx.annotation.StringRes

/** Planned exercise of a template, referenced by catalog slug. */
data class TemplateExercise(
    val slug: String,
    val sets: Int,
    val repsMin: Int,
    val repsMax: Int,
    val restSeconds: Int,
    val group: String = "",
)

data class TemplateRoutine(
    @param:StringRes val name: Int,
    val exercises: List<TemplateExercise>,
)

/** A template: routines and, for programs, the weekday (0 = Monday) of each routine. */
data class PlanTemplate(
    val key: String,
    @param:StringRes val name: Int,
    @param:StringRes val description: Int,
    val routines: List<TemplateRoutine>,
    val schedule: List<Pair<Int, Int>> = emptyList(),
    val weeks: Int = 0,
)
