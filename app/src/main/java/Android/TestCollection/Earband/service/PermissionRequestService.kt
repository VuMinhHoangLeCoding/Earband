package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionRequestService {

    fun checkThenRequestMediaPermission(activity: Activity) : Boolean {
        if( isAudioPermissionGranted(activity) ) {
            return true
        }
        else {
            if (isAndroidVersionHigherOrEqualTiramisu()){
                ActivityCompat.requestPermissions(activity, arrayOf(Constants.PERMISSION_AUDIO), Constants.PERMISSION_REQUEST_CODE)
            }
            else {
                ActivityCompat.requestPermissions(activity, arrayOf(Constants.PERMISSION_EXTERNAL_STORAGE), Constants.PERMISSION_REQUEST_CODE)
            }

            return false
        }
    }

    private fun isAudioPermissionGranted(activity: Activity): Boolean {
        return if (isAndroidVersionHigherOrEqualTiramisu()) {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_AUDIO) == PackageManager.PERMISSION_GRANTED
        }
        else {
            ContextCompat.checkSelfPermission(activity, Constants.PERMISSION_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isAndroidVersionHigherOrEqualTiramisu(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
    }

    fun isPermissionGranted(
        requestCode: Int,
        grantResults: IntArray,
    ) : Boolean {
        if (requestCode == Constants.PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            return grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        return false
    }
}