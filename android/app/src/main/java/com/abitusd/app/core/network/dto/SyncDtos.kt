package com.abitusd.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

/** Wire contract of POST /v1/sync/push and GET /v1/sync/pull (07-BACKEND.md). */
@Serializable
data class OwnExerciseDto(
    val id: String,
    val disciplineId: Int,
    val metricType: String,
    val movementPattern: String,
    val difficulty: Int,
    val isUnilateral: Boolean,
    val usesBodyweight: Boolean,
    val defaultRestSeconds: Int,
    val locale: String,
    val name: String,
    val notes: String,
    val muscles: List<OwnMuscleDto>,
    val equipmentIds: List<Int>,
    val updatedAt: String,
)

@Serializable
data class OwnMuscleDto(
    val muscleGroupId: Int,
    val role: String,
)

@Serializable
data class DeletedRowDto(
    val id: String,
    val updatedAt: String,
)

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("op")
sealed interface PushChangeDto {
    val table: String

    @Serializable
    @SerialName("upsert")
    data class Upsert(
        override val table: String = "exercises",
        val row: OwnExerciseDto,
    ) : PushChangeDto

    @Serializable
    @SerialName("delete")
    data class Delete(
        override val table: String = "exercises",
        val row: DeletedRowDto,
    ) : PushChangeDto
}

@Serializable
data class PushRequest(
    val changes: List<PushChangeDto>,
)

@Serializable
data class RejectedDto(
    val id: String,
    val reason: String,
)

@Serializable
data class PushResponse(
    val accepted: List<String>,
    val rejected: List<RejectedDto>,
    val serverTime: String,
)

@Serializable
data class PullChangeDto(
    val table: String,
    val op: String,
    val serverSeq: Long,
    val row: OwnExerciseDto,
)

@Serializable
data class PullResponse(
    val changes: List<PullChangeDto>,
    val hasMore: Boolean,
    val nextCursor: Long,
    val minValidCursor: Long,
)
