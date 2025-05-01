package dev.brahmkshatriya.facevalue.models

import androidx.recyclerview.widget.DiffUtil

data class Image(
    val repoId: String,
    val holder: ImageHolder,
    val faces: List<Pair<DetectedFace, Face?>>?
) {
    object DiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.holder.id == newItem.holder.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}
