package dev.brahmkshatriya.facevalue.data.repos

import dev.brahmkshatriya.facevalue.models.ImageHolder

interface ImageRepository {
    val name: String
    suspend fun getImages(): List<ImageHolder>
}