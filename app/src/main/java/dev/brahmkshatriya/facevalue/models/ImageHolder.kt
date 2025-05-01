package dev.brahmkshatriya.facevalue.models

import kotlinx.serialization.Serializable

@Serializable
sealed class ImageHolder {

    abstract val id: String

    @Serializable
    data class UrlRequestImageHolder(val request: Request, override val id: String) : ImageHolder()

    @Serializable
    data class UriImageHolder(val uri: String, override val id: String) : ImageHolder()

    companion object {
        fun String.toUrlRequestImageHolder(id: String, headers: Map<String, String> = emptyMap()) =
            UrlRequestImageHolder(Request(url = this, headers = headers), id)

        fun String.toUriImageHolder() = UriImageHolder(this, this)
    }
}