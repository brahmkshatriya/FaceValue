package dev.brahmkshatriya.facevalue.data.db.entities

import androidx.room.Entity

@Entity(primaryKeys = ["boundId"])
data class DetectedFaceEntity(
    val boundId: Long,
    val faceId: Long,
)