package dev.brahmkshatriya.facevalue.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigEntity(
    @PrimaryKey
    val id: Long,
    val selectedRepo: String,
    val loaded: Boolean,
)