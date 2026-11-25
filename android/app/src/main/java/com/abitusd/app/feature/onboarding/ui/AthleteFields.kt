package com.abitusd.app.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.AppMultiDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.NumberField

@Composable
fun AthleteBody(
    p: AthleteProfileEntity,
    change: (AthleteProfileEntity) -> Unit,
) {
    NumberField(
        p.birthYear,
        stringResource(R.string.athlete_birth_year),
        { change(p.copy(birthYear = it.toIntOrNull())) },
    )
    NumberField(p.heightM?.times(CENTIMETERS), stringResource(R.string.athlete_height), {
        change(p.copy(heightM = it.toDoubleOrNull()?.div(CENTIMETERS)))
    }, decimal = true)
    NumberField(p.weightKg, stringResource(R.string.athlete_weight), {
        change(p.copy(weightKg = it.toDoubleOrNull()))
    }, decimal = true)
    AthleteChoice(R.string.athlete_sex, R.array.athlete_sexes, "female,male,undisclosed", p.sex) {
        change(p.copy(sex = it))
    }
}

@Composable
fun AthleteExperience(
    p: AthleteProfileEntity,
    change: (AthleteProfileEntity) -> Unit,
) {
    NumberField(p.trainingYears, stringResource(R.string.athlete_years), {
        change(p.copy(trainingYears = it.toIntOrNull()))
    })
    AthleteChoice(R.string.athlete_level, R.array.athlete_levels, "beginner,intermediate,advanced", p.level) {
        change(p.copy(level = it))
    }
    AthleteMulti(
        R.string.athlete_disciplines,
        R.array.athlete_sports,
        "strength,calisthenics,running,swimming,mobility",
        p.disciplines,
    ) {
        change(p.copy(disciplines = it))
    }
    if ("calisthenics" in p.disciplines.split(',')) {
        com.abitusd.app.feature.profile.ui
            .EnglishNamesPicker()
    }
}

@Composable
fun AthleteGoals(
    p: AthleteProfileEntity,
    change: (AthleteProfileEntity) -> Unit,
) {
    AthleteChoice(R.string.athlete_goal, R.array.athlete_goals_options, GOALS, p.goal) { change(p.copy(goal = it)) }
    AthleteChoice(R.string.athlete_secondary_goal, R.array.athlete_goals_options, GOALS, p.secondaryGoal) {
        change(p.copy(secondaryGoal = it))
    }
    NumberField(
        p.daysPerWeek,
        stringResource(R.string.athlete_days),
        { change(p.copy(daysPerWeek = it.toIntOrNull())) },
    )
    NumberField(p.sessionDurationS?.div(SECONDS), stringResource(R.string.athlete_minutes), {
        change(p.copy(sessionDurationS = it.toIntOrNull()?.times(SECONDS)))
    })
    AthleteChoice(R.string.athlete_place, R.array.athlete_places, "gym,home,park,other", p.place) {
        change(p.copy(place = it))
    }
    AthleteMulti(
        R.string.athlete_equipment,
        R.array.athlete_gear,
        "barbell,dumbbell,pull_up_bar,band,bench,bodyweight",
        p.equipment,
    ) {
        change(p.copy(equipment = it))
    }
}

@Composable
private fun AthleteChoice(
    label: Int,
    labels: Int,
    keys: String,
    selected: String?,
    select: (String?) -> Unit,
) {
    AppDropdown(
        stringResource(label),
        athleteOptions(labels, keys),
        selected,
        select,
        noneLabel = stringResource(R.string.athlete_not_set),
    )
}

@Composable
private fun AthleteMulti(
    label: Int,
    labels: Int,
    keys: String,
    selected: String,
    select: (String) -> Unit,
) {
    val values = selected.split(',').filter(String::isNotBlank).toSet()
    AppMultiDropdown(stringResource(label), athleteOptions(labels, keys), values, { key ->
        select((if (key in values) values - key else values + key).sorted().joinToString(","))
    })
}

@Composable
private fun athleteOptions(
    labels: Int,
    keys: String,
): List<ChoiceOption<String>> =
    keys.split(',').zip(stringArrayResource(labels)).map { (key, label) -> ChoiceOption(key, label) }

private const val CENTIMETERS = 100.0
private const val SECONDS = 60
private const val GOALS = "general,strength,hypertrophy,endurance,skill"
