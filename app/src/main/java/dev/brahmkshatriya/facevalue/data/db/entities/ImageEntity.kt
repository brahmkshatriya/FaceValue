package dev.brahmkshatriya.facevalue.data.db.entities

import androidx.room.Entity
import dev.brahmkshatriya.facevalue.models.ImageHolder
import dev.brahmkshatriya.facevalue.utils.Serializer.toData
import dev.brahmkshatriya.facevalue.utils.Serializer.toJson

@Entity(primaryKeys = ["id", "repoId"])
data class ImageEntity(
    val id: String,
    val repoId: String,
    val order: Int,
    val imageHolderData: String,
    val facesDetected: Boolean = false
) {
    val imageHolder by lazy {
        imageHolderData.toData<ImageHolder>()
    }

    companion object {
        fun ImageHolder.toEntity(repoId: String, order: Int, detected: Boolean): ImageEntity {
            return ImageEntity(
                id = id,
                repoId = repoId,
                order = order,
                imageHolderData = this.toJson(),
                facesDetected = detected,
            )
        }
    }
}
