package Android.TestCollection.Earband.repository

import Android.TestCollection.Earband.model.Audio
import android.content.Context

interface Repository  {

}

class RealRepository(
    private val context: Context,
    private val audioRepository: AudioRepository,
    private val roomRepository: RoomRepository
) : Repository {

}