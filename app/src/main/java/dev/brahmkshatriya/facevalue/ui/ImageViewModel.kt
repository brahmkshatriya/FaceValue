package dev.brahmkshatriya.facevalue.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.brahmkshatriya.facevalue.data.db.ImageDatabase
import dev.brahmkshatriya.facevalue.data.repos.ImageRepository
import dev.brahmkshatriya.facevalue.data.worker.LoadImagesWorker
import dev.brahmkshatriya.facevalue.models.Face
import dev.brahmkshatriya.facevalue.models.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(
    val context: Application,
    val imageRepository: List<ImageRepository>,
    val db: ImageDatabase,
) : ViewModel() {

    var currentImageIndex = MutableStateFlow(0)
    val selectedRepo = MutableStateFlow<String?>(null)
    val images = MutableStateFlow<State>(State.Initialized)

    sealed interface State {
        data object Initialized : State
        data object Loading : State
        data class Final(val images: List<Image>) : State
    }

    private val workManager by lazy {
        WorkManager.getInstance(context)
    }

    fun refresh(force: Boolean, name: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        if (images.value is State.Loading) return@launch
        if (!force && images.value is State.Final) return@launch
        selectedRepo.value?.let { workManager.cancelAllWorkByTag(it) }
        val repo = name ?: selectedRepo.value ?: imageRepository.first().name

        selectedRepo.value = repo
        val request = OneTimeWorkRequestBuilder<LoadImagesWorker>()
            .setInputData(Data.Builder().putString("repoId", repo).build())
            .addTag(repo)
            .build()
        workManager.enqueueUniqueWork("loadImages", ExistingWorkPolicy.REPLACE, request)
    }

    val faces = db.faceFlow.map { list -> list.map { it.face } }

    fun selectFace(detectedFaceId: Long, face: Face) = viewModelScope.launch(Dispatchers.IO) {
        db.insertFaceForDetected(detectedFaceId, face.id)
    }

    fun deleteFace(face: Face) = viewModelScope.launch(Dispatchers.IO) {
        db.deleteFace(face)
    }

    fun addFace(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val face = Face(
            id = 0,
            name = name,
            color = null
        )
        db.insertFace(face)
    }

    fun updateFace(face: Face) = viewModelScope.launch(Dispatchers.IO) {
        db.insertFace(face)
    }

    suspend fun getDetectedFace(detectedFaceId: Long) = withContext(Dispatchers.IO) {
        db.getDetectedFace(detectedFaceId)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                val selected = db.getConfig()?.selectedRepo
                refresh(false, selected)
            }
            db.mainFlow.collectLatest { (selected, list) ->
                selectedRepo.value = selected
                images.value = when (list) {
                    null -> State.Loading
                    else -> State.Final(list)
                }
            }
        }
    }
}
