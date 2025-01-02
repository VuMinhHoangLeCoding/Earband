package Android.TestCollection.Earband

import Android.TestCollection.Earband.model.Audio
import android.content.Context
import android.content.Intent
import android.widget.Toast

object Util {
    fun triggerToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun isAndroidVersionHigherOrEqualTiramisu(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
    }


    fun broadcastNewAudio(context: Context, audio: Audio, action: String) {
        val intent = Intent(action).apply {
            putExtra("NEW_AUDIO", audio)
        }
        context.sendBroadcast(intent)
    }

    fun broadcastState(context: Context, action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }
}