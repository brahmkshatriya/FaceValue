package dev.brahmkshatriya.facevalue.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.brahmkshatriya.facevalue.models.Face

@Entity
data class FaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val color: Int?
) {
    val face by lazy {
        Face(id, name, color)
    }

    companion object {
        fun Face.toEntity() = FaceEntity(
            id = id,
            name = name,
            color = color
        )
    }
}
