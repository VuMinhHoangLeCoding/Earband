package Android.TestCollection.Earband

import Android.TestCollection.Earband.model.Audio
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast

object Util {

    var theme: String = "MUEL"

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

interface FragmentListener {
    fun callbackTriggerFragmentPlayer()
}