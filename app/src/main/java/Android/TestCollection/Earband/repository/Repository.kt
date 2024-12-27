package Android.TestCollection.Earband.repository

import Android.TestCollection.Earband.model.Audio
import android.content.Context

interface Repository  {

    suspend fun allAudiosMedia(): List<Audio>

}

class RealRepository(
    private val context: Context,
    private val audioRepository: AudioRepository
) : Repository {

    override suspend fun allAudiosMedia(): List<Audio> = audioRepository.audios()

}