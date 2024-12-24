package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.databinding.ActivityMainBinding
import Android.TestCollection.Earband.service.PermissionRequestService
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val permissionRequestService = PermissionRequestService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionRequestService = PermissionRequestService()
        permissionRequestService.checkThenRequestMediaPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val hasPermission = permissionRequestService.isPermissionGranted(requestCode, grantResults)
        if(hasPermission){
            Util.triggerToast(this, "Permission granted!")
        }
        else {
            Util.triggerToast(this, "Permission needs to be granted!")
        }
    }
}