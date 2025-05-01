package dev.brahmkshatriya.facevalue.models

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val url: String,
    val headers: Map<String, String> = emptyMap()
)