package com.abitusd.app.feature.train.data

import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.feature.train.domain.MuscleShare
import com.abitusd.app.feature.train.domain.PlannedSets
import com.abitusd.app.feature.train.domain.PlannedVolume
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Planned sets per muscle group with localized names, largest first. */
class VolumeCalculator
    @Inject
    constructor(
        private val library: LibraryDao,
    ) {
        suspend fun perMuscle(
            sets: List<PlannedSets>,
            locale: String,
        ): List<Pair<String, Double>> {
            if (sets.isEmpty()) return emptyList()
            val ids = sets.map { it.exerciseId }.distinct()
            val shares = library.musclesOf(ids).map { MuscleShare(it.exerciseId, it.muscleGroupId, it.contribution) }
            val names = library.muscleGroups(locale).first().associate { it.id to it.name }
            val volume = PlannedVolume.perMuscle(sets, shares).entries.sortedByDescending { it.value }
            return volume.map { names[it.key].orEmpty() to it.value }
        }
    }
