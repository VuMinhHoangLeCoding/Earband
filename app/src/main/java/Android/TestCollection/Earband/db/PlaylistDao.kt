package Android.TestCollection.Earband.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Query("UPDATE PlaylistEntity SET playlist_name = :name WHERE playlist_id = :playlistId")
    fun renamePlaylist(name: String, playlistId: Long)

    @Query("SELECT * FROM PlaylistEntity")
    suspend fun getAllPlaylist(): List<PlaylistEntity>

    @Query("SELECT * FROM PlaylistEntity WHERE playlist_name = :name")
    fun getPlaylistOnName(name: String): List<PlaylistEntity>



    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    suspend fun getAllPlaylistsWithAudios(): List<PlaylistOneToManyAudio>

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE playlist_id = :playlistId")
    fun getPlaylistWithAudiosOnId(playlistId: Long):LiveData<PlaylistOneToManyAudio>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudiosToPlaylist(audioEntities: List<AudioEntity>)

    @Query("DELETE FROM AudioEntity WHERE playlist_creator_id = :playlistId AND id = :audioId")
    suspend fun deleteAudioFromPlaylist(audioId: Long, playlistId: Long)

    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun deletePlaylistAudios(audios: List<AudioEntity>)

    @Query("SELECT EXISTS(SELECT * FROM PlaylistEntity WHERE playlist_id = :playlistId)")
    fun hasPlaylist(playlistId: Long): LiveData<Boolean>

}