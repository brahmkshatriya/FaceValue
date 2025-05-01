package dev.brahmkshatriya.facevalue.data.repos.local

import android.content.Context
import android.provider.MediaStore
import dev.brahmkshatriya.facevalue.data.repos.ImageRepository
import dev.brahmkshatriya.facevalue.models.ImageHolder
import dev.brahmkshatriya.facevalue.models.ImageHolder.Companion.toUriImageHolder

class LocalImageRepository(
    private val context: Context
) : ImageRepository {
    override val name = "Local"

    override suspend fun getImages(block: suspend (ImageHolder) -> Unit) {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.MediaColumns.DATA),
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )!!
        cursor.use {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            while (it.moveToNext()) {
                val imagePath = it.getString(columnIndex)
                block(imagePath.toUriImageHolder())
            }
        }
    }
}