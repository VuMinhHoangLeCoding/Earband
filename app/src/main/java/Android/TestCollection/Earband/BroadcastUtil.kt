package Android.TestCollection.Earband

import Android.TestCollection.Earband.model.Audio
import android.content.Context
import android.content.Intent

class BroadcastUtil {

    fun broadcastNewAudio(context: Context, audio: Audio, action: String) {
        val intent = Intent(action).apply {
            putExtra("NEW_AUDIO", audio)
        }
        context.sendBroadcast(intent)
    }

    fun broadcastPlayerState(context: Context, action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }
}