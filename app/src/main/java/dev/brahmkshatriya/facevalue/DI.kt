package dev.brahmkshatriya.facevalue

import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector.FaceDetectorOptions
import dev.brahmkshatriya.facevalue.data.db.ImageDatabase
import dev.brahmkshatriya.facevalue.data.repos.local.LocalImageRepository
import dev.brahmkshatriya.facevalue.data.repos.pixabay.PixabayImageRepository
import dev.brahmkshatriya.facevalue.data.worker.DetectFacesWorker
import dev.brahmkshatriya.facevalue.data.worker.LoadImagesWorker
import dev.brahmkshatriya.facevalue.ui.ImageViewModel
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object DI {
    val appModule = module {
        singleOf(ImageDatabase::create)
        single {
            listOf(
                LocalImageRepository(get()),
                PixabayImageRepository(),
            )
        }
        single {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(BuildConfig.FACE_DETECTION_MODEL)
                .setDelegate(DELEGATE)
                .build()
            val options = FaceDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinDetectionConfidence(THRESHOLD)
                .setRunningMode(RUNNING_MODE)
                .build()
            FaceDetector.createFromOptions(get(), options)
        }
        workerOf(::LoadImagesWorker)
        workerOf(::DetectFacesWorker)
        viewModelOf(::ImageViewModel)
    }

    private val DELEGATE = Delegate.CPU
    private val RUNNING_MODE = RunningMode.IMAGE
    private const val THRESHOLD = 0.55f
}