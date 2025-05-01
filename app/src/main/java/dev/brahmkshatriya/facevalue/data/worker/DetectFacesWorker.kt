package dev.brahmkshatriya.facevalue.data.worker

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import dev.brahmkshatriya.facevalue.data.db.ImageDatabase
import dev.brahmkshatriya.facevalue.models.DetectedFace
import dev.brahmkshatriya.facevalue.utils.ImageUtils.loadDrawable

class DetectFacesWorker(
    private val context: Context,
    params: WorkerParameters,
    private val db: ImageDatabase,
    private val faceDetector: FaceDetector
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repoId = inputData.getString("repoId") ?: return Result.failure()
        val imageId = inputData.getString("imageId") ?: return Result.failure()

        val (image, detected) = db.getImageHolder(repoId, imageId)
        if (detected == true) return Result.success()
        if (image == null) return Result.failure()
        val bitmap = image.loadDrawable(context)?.toBitmap() ?: return Result.failure()

        val result = faceDetector.detect(BitmapImageBuilder(bitmap).build())!!

        val faces = result.detections().map {
            val score = it.categories().first().score()
            val box = it.boundingBox()
            DetectedFace(
                score = score,
                x = box.left.toInt(),
                y = box.top.toInt(),
                width = box.width().toInt(),
                height = box.height().toInt()
            )
        }
        db.insertImage(repoId, image, true)
        faces.forEach { db.insertDetectedFace(imageId, it) }
        return Result.success()
    }
}