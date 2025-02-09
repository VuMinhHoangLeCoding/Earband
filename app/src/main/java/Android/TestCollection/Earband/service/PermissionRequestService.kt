package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Permission
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
                Permission.AUDIO,
                Permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
                Permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                Permission.EXTERNAL_STORAGE,
                Permission.FOREGROUND_SERVICE
            )
        }
        ActivityCompat.requestPermissions(activity, permissions, Permission.REQUEST_CODE)
    }

    private fun isAudioPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Permission.AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(activity, Permission.EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isForegroundServiceMediaPlaybackPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(activity, Permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isPostNotificationPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
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
        if (requestCode == Permission.REQUEST_CODE && grantResults.isNotEmpty()) {
            return grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        return false
    }
}