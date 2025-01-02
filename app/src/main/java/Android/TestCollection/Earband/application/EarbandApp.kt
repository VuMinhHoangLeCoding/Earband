package Android.TestCollection.Earband.application

import Android.TestCollection.Earband.db.EarbandDatabase
import Android.TestCollection.Earband.repository.RealAudioRepository
import Android.TestCollection.Earband.repository.RealRepository
import Android.TestCollection.Earband.repository.RealRoomRepository
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class EarbandApp : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    val database by lazy { EarbandDatabase.getDatabase(this, applicationScope) }
    val roomRepository by lazy { RealRoomRepository(database.playlistDao(), database.audioHistoryDao()) }
    val audioRepository by lazy { RealAudioRepository(applicationContext) }
    val repository by lazy { RealRepository(applicationContext, audioRepository, roomRepository) }

    val appAudioPlayerData by lazy { AppAudioPlayerData(audioRepository, applicationScope) }
}