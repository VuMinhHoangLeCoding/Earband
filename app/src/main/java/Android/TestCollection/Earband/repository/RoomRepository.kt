package Android.TestCollection.Earband.repository

import Android.TestCollection.Earband.db.AudioHistoryDao
import Android.TestCollection.Earband.db.PlaylistDao
import Android.TestCollection.Earband.db.PlaylistEntity
import Android.TestCollection.Earband.db.PlaylistOneToManyAudio
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

interface RoomRepository {

}

class RealRoomRepository(
    private val playlistDao: PlaylistDao,
    private val audioHistoryDao: AudioHistoryDao
) : RoomRepository {

    @WorkerThread
    suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long = playlistDao.insertPlaylist(playlistEntity)

    @WorkerThread
    suspend fun getPlaylistWithAudios(playlistId: Long): LiveData<PlaylistOneToManyAudio> = playlistDao.getPlaylistWithAudios(playlistId)

    @WorkerThread
    fun hasPlaylist(playlistId: Long): LiveData<Boolean> = playlistDao.hasPlaylist(playlistId)



}