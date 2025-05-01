package dev.brahmkshatriya.facevalue.models

data class DetectedFace(
    val id: Long = 0,
    val score: Float,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)
