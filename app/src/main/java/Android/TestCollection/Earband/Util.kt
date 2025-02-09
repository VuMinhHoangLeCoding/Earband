package Android.TestCollection.Earband

import Android.TestCollection.Earband.model.Audio
import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi

object Util {

    var theme: String = "MUD"

    val baseProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.COMPOSER
    )

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

    fun broadcastAction(context: Context, action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }

    fun broadcastBoolean(context: Context, boolean: Boolean, action: String) {
        val intent = Intent(action).apply {
            putExtra("BOOLEAN", boolean)
        }
        context.sendBroadcast(intent)
    }

    fun broadcastString(context: Context, string: String, action: String) {
        val intent = Intent(action).apply {
            putExtra("STRING", string)
        }
        context.sendBroadcast(intent)
    }

    fun broadPlayerProgress(context: Context, action: String, progress: Long) {
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


//    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        val view = currentFocus //currentFocus: retrieve current focus view
//        if (view is TextInputEditText || view is TextInputLayout) { //check if the current view is the textbox or not
//            val rect = Rect() //Rect() - rectangle - is a class for coordination of something (view / items in the  UI, ...)
//            view.getGlobalVisibleRect(rect) //get the coordinate of the view, which is the view of the textbox
//            if (!rect.contains(event!!.rawX.toInt(), event.rawY.toInt())) {
//                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//                view.clearFocus()
//
//                if (view is TextInputEditText) {
//                    view.text?.clear()
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }

interface MainDrawerHandler {
    fun openDrawer()
}

interface CallbackMainShuffle {
    fun triggerShuffle()
}

interface CallbackDropdownMenu {
    fun triggerOpenDropdownMenu()
}

interface FragmentListener {
    fun callbackTriggerFragmentPlayer()
}

interface BroadcastAction {
    companion object {
        const val AUDIO_SELECTED: String = "AUDIO_SELECTED"
        const val FROM_MINI_PLAYER: String = "FROM_MINI_PLAYER"
        const val AUDIO_ENDED: String = "AUDIO_ENDED"
        const val CONFIRM_BACKWARD: String = "CONFIRM_BACKWARD"
        const val PLAYER_PROGRESSION: String = "PLAYER_PROGRESSION"
        const val SEEKBAR_PROGRESSION_CHANGES: String = "SEEKBAR_PROGRESSION_CHANGES"
        const val SHUFFLE: String = "SHUFFLE"
        const val FROM_PLAYER: String = "FROM_PLAYER"
        const val LOSE_FOCUS: String = "LOSE_FOCUS"
    }
}

interface Permission {
    companion object {
        const val REQUEST_CODE = 100

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val AUDIO = permission.READ_MEDIA_AUDIO

        const val EXTERNAL_STORAGE = permission.READ_EXTERNAL_STORAGE

        @RequiresApi(Build.VERSION_CODES.P)
        const val FOREGROUND_SERVICE = permission.FOREGROUND_SERVICE

        @SuppressLint("InlinedApi")
        const val FOREGROUND_SERVICE_MEDIA_PLAYBACK = permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK

        @SuppressLint("InlinedApi")
        const val POST_NOTIFICATIONS = permission.POST_NOTIFICATIONS
    }
}

interface AudioSortOrder {
    companion object {
        const val AUDIO_DEFAULT = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

        const val AUDIO_A_Z = MediaStore.Audio.Media.TITLE

        const val AUDIO_Z_A = "$AUDIO_A_Z DESC"

        const val AUDIO_YEAR = MediaStore.Audio.Media.YEAR + " DESC"

        const val AUDIO_DURATION = MediaStore.Audio.Media.DURATION + " DESC"

        const val AUDIO_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC"

        const val AUDIO_DATE_MODIFIED = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"
    }
}