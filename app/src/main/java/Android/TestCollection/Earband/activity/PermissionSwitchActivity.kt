package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.databinding.ActivityPermissionRequestBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PermissionSwitchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

}