package dev.brahmkshatriya.facevalue.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.brahmkshatriya.facevalue.data.db.entities.ConfigEntity
import dev.brahmkshatriya.facevalue.data.db.entities.DetectedBoundsEntity
import dev.brahmkshatriya.facevalue.data.db.entities.DetectedBoundsEntity.Companion.toEntity
import dev.brahmkshatriya.facevalue.data.db.entities.DetectedFaceEntity
import dev.brahmkshatriya.facevalue.data.db.entities.FaceEntity
import dev.brahmkshatriya.facevalue.data.db.entities.FaceEntity.Companion.toEntity
import dev.brahmkshatriya.facevalue.data.db.entities.ImageEntity
import dev.brahmkshatriya.facevalue.data.db.entities.ImageEntity.Companion.toEntity
import dev.brahmkshatriya.facevalue.models.DetectedFace
import dev.brahmkshatriya.facevalue.models.Face
import dev.brahmkshatriya.facevalue.models.Image
import dev.brahmkshatriya.facevalue.models.ImageHolder
import kotlinx.coroutines.flow.combine

@Database(
    entities = [
        ConfigEntity::class,
        ImageEntity::class,
        DetectedBoundsEntity::class,
        FaceEntity::class,
        DetectedFaceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun faceDao(): FaceDao

    private val dao by lazy { faceDao() }

    val faceFlow by lazy { dao.facesFlow() }

    private val imageFlow by lazy {
        combine(
            dao.imagesFlow(),
            dao.detectedBoundsFlow(),
            faceFlow,
            dao.detectedFacesFlow()
        ) { images, bounds, faces, detectedFaces ->
            val boundMap = bounds.groupBy { it.imageId }
            val detectedFaceMap = detectedFaces.groupBy { it.boundId }
            val faceMap = faces.associateBy { it.id }
            images.sortedBy { it.order }.map { image ->
                val imageFaces = boundMap[image.id]?.map { bounds ->
                    val detectedFaceId =
                        detectedFaceMap[bounds.id]?.map { it.faceId }.orEmpty().firstOrNull()
                    val face = faceMap[detectedFaceId]?.face
                    bounds.detectedFace to face
                }.orEmpty()
                Image(
                    repoId = image.repoId,
                    holder = image.imageHolder,
                    faces = imageFaces.takeIf { image.facesDetected },
                )
            }
        }
    }

    val mainFlow by lazy {
        combine(
            dao.configFlow(),
            imageFlow
        ) { config, images ->
            val selected = config?.selectedRepo
            val filtered = if (config?.loaded != true) null
            else selected?.let { repo -> images.filter { it.repoId == repo } }
            selected to filtered
        }
    }

    suspend fun insertFace(face: Face) {
        dao.insertFace(face.toEntity())
    }

    suspend fun insertImage(repoId: String, image: ImageHolder, detected: Boolean) {
        val order = dao.getImage(repoId, image.id)?.order ?: dao.getImageCount(repoId)
        dao.insertImage(image.toEntity(repoId, order, detected))
    }

    suspend fun insertDetectedFace(imageId: String, detectedFace: DetectedFace) {
        dao.insertDetectedBounds(detectedFace.toEntity(imageId))
    }

    suspend fun insertFaceForDetected(detectedFaceId: Long, faceId: Long) {
        dao.insertDetectedFaces(DetectedFaceEntity(detectedFaceId, faceId))
    }

    suspend fun getImageHolder(repoId: String, imageId: String): Pair<ImageHolder?, Boolean?> {
        return dao.getImage(repoId, imageId).let {
            it?.imageHolder to it?.facesDetected
        }
    }

    suspend fun insertConfig(repoId: String, loaded: Boolean) {
        dao.insertConfig(ConfigEntity(0, repoId, loaded))
    }

    suspend fun getConfig(): ConfigEntity? {
        return dao.getConfig()
    }

    suspend fun getAllImages(repoId: String): List<ImageEntity> {
        return dao.getAllImages(repoId)
    }

    suspend fun deleteFace(face: Face) {
        dao.deleteFace(face.toEntity())
    }

    suspend fun getDetectedFace(detectedFaceId: Long): DetectedFaceEntity? {
        return dao.getDetectedFace(detectedFaceId)
    }

    companion object {
        private const val DATABASE_NAME = "image-database"
        fun create(app: Application) = Room.databaseBuilder(
            app, ImageDatabase::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigration(true).build()
    }
}