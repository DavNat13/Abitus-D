package com.abitusd.app.feature.train.domain

import androidx.annotation.StringRes
import com.abitusd.app.R

/** Basic templates (10b, Fase 3). Standard set and rep ranges for general strength. */
object TemplateCatalog {
    private fun e(
        slug: String,
        sets: Int,
        min: Int,
        max: Int,
        rest: Int,
        group: String = "",
    ) = TemplateExercise(slug, sets, min, max, rest, group)

    private val fullBody =
        listOf(
            TemplateRoutine(
                R.string.tpl_full_body_a,
                listOf(
                    e("barbell_back_squat", 3, 5, 8, 180),
                    e("barbell_bench_press", 3, 5, 8, 180),
                    e("barbell_bent_over_row", 3, 6, 10, 120),
                    e("plank", 3, 30, 45, 60),
                ),
            ),
            TemplateRoutine(
                R.string.tpl_full_body_b,
                listOf(
                    e("romanian_deadlift", 3, 6, 10, 150),
                    e("standing_overhead_press", 3, 5, 8, 150),
                    e("pull_up", 3, 5, 10, 120),
                    e("walking_lunge", 3, 10, 12, 90),
                ),
            ),
            TemplateRoutine(
                R.string.tpl_full_body_c,
                listOf(
                    e("conventional_deadlift", 3, 3, 5, 180),
                    e("incline_dumbbell_press", 3, 8, 12, 120),
                    e("seated_cable_row", 3, 10, 12, 90),
                    e("hanging_leg_raise", 3, 8, 12, 60),
                ),
            ),
        )

    private val ppl =
        listOf(
            TemplateRoutine(
                R.string.tpl_push,
                listOf(
                    e("barbell_bench_press", 4, 5, 8, 180),
                    e("standing_overhead_press", 3, 6, 10, 150),
                    e("incline_dumbbell_press", 3, 8, 12, 120),
                    e("dumbbell_lateral_raise", 3, 12, 15, 60),
                    e("triceps_pushdown", 3, 10, 15, 60),
                ),
            ),
            TemplateRoutine(
                R.string.tpl_pull,
                listOf(
                    e("pull_up", 4, 5, 10, 150),
                    e("barbell_bent_over_row", 3, 6, 10, 120),
                    e("face_pull", 3, 12, 15, 60),
                    e("barbell_curl", 3, 8, 12, 60),
                    e("hammer_curl", 3, 10, 12, 60),
                ),
            ),
            TemplateRoutine(
                R.string.tpl_legs,
                listOf(
                    e("barbell_back_squat", 4, 5, 8, 180),
                    e("romanian_deadlift", 3, 6, 10, 150),
                    e("leg_press", 3, 10, 12, 120),
                    e("lying_leg_curl", 3, 10, 12, 60),
                    e("standing_calf_raise", 3, 10, 15, 60),
                ),
            ),
        )

    val all: List<PlanTemplate> =
        listOf(
            PlanTemplate(
                "full_body_3",
                R.string.tpl_full_body,
                R.string.tpl_full_body_desc,
                fullBody,
                listOf(0 to 0, 2 to 1, 4 to 2),
                weeks = 4,
            ),
            PlanTemplate(
                "ppl",
                R.string.tpl_ppl,
                R.string.tpl_ppl_desc,
                ppl,
                listOf(0 to 0, 1 to 1, 2 to 2, 4 to 0, 5 to 1, 6 to 2),
                weeks = 4,
            ),
            PlanTemplate(
                "express_20",
                R.string.tpl_express,
                R.string.tpl_express_desc,
                listOf(
                    TemplateRoutine(
                        R.string.tpl_express,
                        listOf(
                            e("goblet_squat", 3, 10, 12, 45, "A"),
                            e("push_up", 3, 8, 15, 45, "A"),
                            e("one_arm_dumbbell_row", 3, 10, 12, 45, "B"),
                            e("plank", 3, 30, 40, 45, "B"),
                        ),
                    ),
                ),
            ),
            PlanTemplate(
                "home_no_equipment",
                R.string.tpl_home,
                R.string.tpl_home_desc,
                listOf(
                    TemplateRoutine(
                        R.string.tpl_home,
                        listOf(
                            e("bodyweight_squat", 3, 15, 20, 60),
                            e("push_up", 3, 8, 15, 60),
                            e("walking_lunge", 3, 10, 12, 60),
                            e("single_leg_glute_bridge", 3, 10, 15, 45),
                            e("superman", 3, 20, 30, 45),
                            e("plank", 3, 30, 45, 45),
                        ),
                    ),
                ),
            ),
        )
}
