package dev.brahmkshatriya.facevalue.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.brahmkshatriya.facevalue.data.db.entities.ConfigEntity
import dev.brahmkshatriya.facevalue.data.db.entities.DetectedBoundsEntity
import dev.brahmkshatriya.facevalue.data.db.entities.DetectedFaceEntity
import dev.brahmkshatriya.facevalue.data.db.entities.FaceEntity
import dev.brahmkshatriya.facevalue.data.db.entities.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceDao {

    @Query("SELECT * FROM ConfigEntity")
    fun configFlow(): Flow<ConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Query("SELECT * FROM ConfigEntity")
    suspend fun getConfig(): ConfigEntity?

    @Query("SELECT * FROM ImageEntity")
    fun imagesFlow(): Flow<List<ImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Query("SELECT * FROM ImageEntity WHERE id = :id AND repoId = :repoId")
    suspend fun getImage(repoId: String, id: String): ImageEntity?

    @Query("SELECT * FROM DetectedBoundsEntity")
    fun detectedBoundsFlow(): Flow<List<DetectedBoundsEntity>>

    @Insert
    suspend fun insertDetectedBounds(detectedBounds: DetectedBoundsEntity)

    @Query("SELECT * FROM FaceEntity")
    fun facesFlow(): Flow<List<FaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFace(face: FaceEntity)

    @Query("SELECT * FROM DetectedFaceEntity")
    fun detectedFacesFlow(): Flow<List<DetectedFaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetectedFaces(detectedFaces: DetectedFaceEntity)

    @Query("SELECT COUNT(*) FROM ImageEntity WHERE repoId = :repoId")
    suspend fun getImageCount(repoId: String): Int

    @Query("SELECT * FROM ImageEntity WHERE repoId = :repoId")
    suspend fun getAllImages(repoId: String): List<ImageEntity>

    @Delete
    suspend fun deleteFace(face: FaceEntity)

    @Query("SELECT * FROM DetectedFaceEntity WHERE boundId = :detectedFaceId")
    suspend fun getDetectedFace(detectedFaceId: Long): DetectedFaceEntity?
}