package dev.brahmkshatriya.facevalue.data.worker

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.brahmkshatriya.facevalue.data.db.ImageDatabase
import dev.brahmkshatriya.facevalue.data.repos.ImageRepository

class LoadImagesWorker(
    context: Context,
    params: WorkerParameters,
    private val imageRepos: List<ImageRepository>,
    private val db: ImageDatabase
) : CoroutineWorker(context, params) {

    private val workManager by lazy { WorkManager.getInstance(context) }
    override suspend fun doWork(): Result {
        val repoId = inputData.getString("repoId") ?: return Result.failure()
        val repo = imageRepos.find { it.name == repoId } ?: return Result.failure()

        fun enqueueDetectFaces(imageId: String) {
            val request = OneTimeWorkRequestBuilder<DetectFacesWorker>()
                .setInputData(
                    Data.Builder().putString("repoId", repoId).putString("imageId", imageId).build()
                )
                .addTag(repoId)
                .build()
            workManager.enqueueUniqueWork(
                "$repoId:$imageId", ExistingWorkPolicy.KEEP, request
            )
        }

        db.insertConfig(repoId, false)
        runCatching {
            repo.getImages {
                val detected = db.getImageHolder(repoId, it.id).second ?: false
                db.insertImage(repoId, it, detected)
                if (!detected) enqueueDetectFaces(it.id)
            }
        }.getOrElse {
            it.printStackTrace()
            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
            return Result.failure()
        }
        db.insertConfig(repoId, true)

        db.getAllImages(repoId).filter { !it.facesDetected }.forEach {
            enqueueDetectFaces(it.id)
        }

        return Result.success()
    }

}