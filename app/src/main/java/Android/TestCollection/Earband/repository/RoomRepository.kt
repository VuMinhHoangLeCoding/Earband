package Android.TestCollection.Earband.repository

import Android.TestCollection.Earband.db.AudioEntity
import Android.TestCollection.Earband.db.AudioHistoryDao
import Android.TestCollection.Earband.db.AudioHistoryEntity
import Android.TestCollection.Earband.db.PlaylistDao
import Android.TestCollection.Earband.db.PlaylistEntity
import Android.TestCollection.Earband.db.PlaylistOneToManyAudio
import Android.TestCollection.Earband.db.UtilityDao
import Android.TestCollection.Earband.db.UtilityEntity
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

interface RoomRepository {

    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long
    fun renamePlaylist(name: String, playlistId: Long)
    suspend fun getAllPlaylist(): List<PlaylistEntity>
    fun getPlaylistOnName(name: String): List<PlaylistEntity>
    suspend fun getAllPlaylistsWithAudios(): List<PlaylistOneToManyAudio>
    fun getPlaylistWithAudiosOnId(playlistId: Long): LiveData<PlaylistOneToManyAudio>
    suspend fun insertAudiosToPlaylist(audioEntities: List<AudioEntity>)
    suspend fun deleteAudioFromPlaylist(audioId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)
    suspend fun deletePlaylistAudios(audios: List<AudioEntity>)
    fun hasPlaylist(playlistId: Long): LiveData<Boolean>

    suspend fun upsertAudioHistory(historyEntity: AudioHistoryEntity)
    suspend fun deleteAudioHistoryOnId(id: Long)
    suspend fun getAudioHistoryList(): List<AudioHistoryEntity>
    fun observableAudioHistoryEntityList(): LiveData<List<AudioHistoryEntity>>
    suspend fun clearAudioHistory()

    suspend fun upsertUtility(utilityEntity: UtilityEntity)
    suspend fun getUtilityEntity(): UtilityEntity?

}

class RealRoomRepository(
    private val playlistDao: PlaylistDao,
    private val audioHistoryDao: AudioHistoryDao,
    private val utilityDao: UtilityDao
) : RoomRepository {

    @WorkerThread       // The task should be put in different thread
    override suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long = playlistDao.insertPlaylist(playlistEntity)      //task can be paused or resumed to not block other task

    override fun renamePlaylist(name: String, playlistId: Long) = playlistDao.renamePlaylist(name, playlistId)

    @WorkerThread
    override suspend fun getAllPlaylist(): List<PlaylistEntity> = playlistDao.getAllPlaylist()

    @WorkerThread
    override suspend fun getAllPlaylistsWithAudios(): List<PlaylistOneToManyAudio> = playlistDao.getAllPlaylistsWithAudios()

    override fun getPlaylistOnName(name: String): List<PlaylistEntity> = playlistDao.getPlaylistOnName(name)

    @WorkerThread
    override fun getPlaylistWithAudiosOnId(playlistId: Long): LiveData<PlaylistOneToManyAudio> = playlistDao.getPlaylistWithAudiosOnId(playlistId)

    @WorkerThread
    override suspend fun insertAudiosToPlaylist(audioEntities: List<AudioEntity>) = playlistDao.insertAudiosToPlaylist(audioEntities)

    override suspend fun deleteAudioFromPlaylist(audioId: Long, playlistId: Long) = playlistDao.deleteAudioFromPlaylist(audioId, playlistId)

    override suspend fun deletePlaylist(playlistEntity: PlaylistEntity) = playlistDao.deletePlaylist(playlistEntity)

    @WorkerThread
    override suspend fun deletePlaylistAudios(audios: List<AudioEntity>) = playlistDao.deletePlaylistAudios(audios)

    override fun hasPlaylist(playlistId: Long): LiveData<Boolean> = playlistDao.hasPlaylist(playlistId)

    override suspend fun upsertAudioHistory(historyEntity: AudioHistoryEntity) = audioHistoryDao.upsertAudioHistory(historyEntity)

    override suspend fun deleteAudioHistoryOnId(id: Long) = audioHistoryDao.deleteAudioHistoryOnId(id)

    override suspend fun getAudioHistoryList(): List<AudioHistoryEntity> = audioHistoryDao.getAudioHistoryList()

    override fun observableAudioHistoryEntityList(): LiveData<List<AudioHistoryEntity>> = audioHistoryDao.observableAudioHistoryEntityList()

    override suspend fun clearAudioHistory() = audioHistoryDao.clearAudioHistory()

    override suspend fun upsertUtility(utilityEntity: UtilityEntity) = utilityDao.upsertUtility(utilityEntity)

    override suspend fun getUtilityEntity(): UtilityEntity? = utilityDao.getUtilityEntity()

}