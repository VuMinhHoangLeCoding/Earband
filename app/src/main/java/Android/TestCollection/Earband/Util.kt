package Android.TestCollection.Earband

import Android.TestCollection.Earband.model.Audio
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast

object Util {
    fun triggerToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isAndroidVersionHigherOrEqualTiramisu(): Boolean {
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

    fun broadcastPlayerProgress(context: Context, action: String, progress: Long) {
        val intent = Intent(action).apply {
            putExtra("PROGRESSION", progress)
        }
        context.sendBroadcast(intent)
    }

    @SuppressLint("DefaultLocale")
    fun formatTime(milliseconds: Long): String {
        val hours = (milliseconds / 1000) / 3600
        val minutes = ((milliseconds / 1000) % 3600) / 60
        val seconds = (milliseconds / 1000) % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}

interface MainDrawerHandler {
    fun openDrawer()
}

interface CallbackMainShuffle {
    fun triggerShuffle()
}