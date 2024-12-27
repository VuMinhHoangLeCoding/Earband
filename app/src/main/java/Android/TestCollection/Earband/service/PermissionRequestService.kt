package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionRequestService {

    fun checkRequiredPermission(activity: Activity): Boolean {
        return isAudioPermissionGranted(activity) &&
               isForegroundServiceMediaPlaybackPermissionGranted(activity) &&
               isPostNotificationPermissionGranted(activity)
    }

    fun requestPermission(activity: Activity) {
        val permissions = if (isAndroidVersionHigherOrEqualTiramisu()) {
            arrayOf(
                Constants.PERMISSION_AUDIO,
                Constants.PERMISSION_FOREGROUND_SERVICE_MEDIA_PLAYBACK,
                Constants.PERMISSION_POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                Constants.PERMISSION_EXTERNAL_STORAGE,
                Constants.PERMISSION_FOREGROUND_SERVICE
            )
        }
        ActivityCompat.requestPermissions(activity, permissions, Constants.PERMISSION_REQUEST_CODE)
    }

    private fun isAudioPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isForegroundServiceMediaPlaybackPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_FOREGROUND_SERVICE_MEDIA_PLAYBACK) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isPostNotificationPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isAndroidVersionHigherOrEqualTiramisu(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
    }

    fun isPermissionGranted(
        requestCode: Int,
        grantResults: IntArray,
    ): Boolean {
        if (requestCode == Constants.PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            return grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        return false
    }
}