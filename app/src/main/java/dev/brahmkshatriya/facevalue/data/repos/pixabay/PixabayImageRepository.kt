package dev.brahmkshatriya.facevalue.data.repos.pixabay

import dev.brahmkshatriya.facevalue.data.repos.ImageRepository
import dev.brahmkshatriya.facevalue.models.ImageHolder
import dev.brahmkshatriya.facevalue.models.ImageHolder.Companion.toUrlRequestImageHolder
import dev.brahmkshatriya.facevalue.utils.ContinuationCallback.Companion.await
import dev.brahmkshatriya.facevalue.utils.Serializer.toData
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request

class PixabayImageRepository : ImageRepository {
    override val name = "Pixabay"

    private val client = OkHttpClient.Builder().build()

    private fun createRequest(keyword: String) = Request.Builder().url(
        "https://pixabay.com/api/?key=$API_KEY&q=$keyword&image_type=photo"
    ).build()

    private suspend fun getImages(keyword: String): List<ImageHolder> {
        val response = client.newCall(createRequest(keyword)).await().body.string()
        val data = response.toData<PixabayResponse>()
        return data.hits!!.mapNotNull {
            it.webformatURL?.toUrlRequestImageHolder(id = it.id.toString())
        }
    }

    override suspend fun getImages(block: suspend (ImageHolder) -> Unit) {
        val pics = getImages("portrait") + getImages("nature")
        pics.shuffled().forEach { block(it) }
    }

    companion object {
        const val API_KEY = "13683446-d2914db94fe0f3b6d8099732b"
    }

    @Serializable
    data class PixabayResponse(
        val hits: List<Hit>? = null
    )

    @Serializable
    data class Hit(val id: Long, val webformatURL: String? = null)
}
