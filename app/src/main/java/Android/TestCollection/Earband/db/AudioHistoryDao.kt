package Android.TestCollection.Earband.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AudioHistoryDao {
    companion object {
        private const val HISTORY_LIMIT = 100
    }

    @Upsert
    suspend fun upsertAudioHistory(historyEntity: AudioHistoryEntity)

    @Query("DELETE FROM AudioHistoryEntity WHERE id = :id")
    fun deleteAudio(id: Long)

    @Query("SELECT * FROM AudioHistoryEntity ORDER BY time_played DESC LIMIT $HISTORY_LIMIT")
    fun historyAudios():List<AudioHistoryEntity>

    @Query("SELECT * FROM AudioHistoryEntity ORDER BY time_played DESC LIMIT $HISTORY_LIMIT")
    fun observableHistoryAudios(): LiveData<List<AudioHistoryEntity>>

    @Query("DELETE FROM AudioHistoryEntity")
    fun clearAudioHistory()
}
