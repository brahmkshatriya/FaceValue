package dev.brahmkshatriya.facevalue.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.brahmkshatriya.facevalue.models.DetectedFace

@Entity
data class DetectedBoundsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val imageId: String,
    val score: Float,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
) {
    val detectedFace by lazy {
        DetectedFace(
            id = id,
            width = width,
            x = x,
            y = y,
            height = height,
            score = score,
        )
    }

    companion object {
        fun DetectedFace.toEntity(imageId: String) = DetectedBoundsEntity(
            id = id,
            imageId = imageId,
            score = score,
            x = x,
            y = y,
            width = width,
            height = height,
        )
    }
}