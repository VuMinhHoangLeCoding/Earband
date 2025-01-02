package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.PermissionCardAdapter
import Android.TestCollection.Earband.databinding.ActivityWelcomePermissionRequestBinding
import Android.TestCollection.Earband.recycler_view_items.PermissionCard
import Android.TestCollection.Earband.service.PermissionRequestService
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class WelcomePermissionRequestActivity : AppCompatActivity() {

    private val permissionRequestService = PermissionRequestService()
    private lateinit var binding: ActivityWelcomePermissionRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permissionRequestService.checkRequiredPermission(this)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivityWelcomePermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewPermission.layoutManager = LinearLayoutManager(this)

        val items = listOf(
            PermissionCard("Audio Permission", "Allow access to audio", false),
            PermissionCard("Permission 2", "Allow 2", false)
        )

        val adapter = PermissionCardAdapter(items) { position, isChecked ->
            val toggledPermission = items[position]
            toggledPermission.isChecked = isChecked
            when (position) {
                0 ->
                    when (isChecked) {
                        true -> {
                            val hasPermission = permissionRequestService.checkRequiredPermission(this)
                            when (hasPermission) {
                                true -> {
                                    binding.buttonBottom.isEnabled = true
                                }

                                false -> {
                                    permissionRequestService.requestPermission(this)
                                    Util.triggerToast(
                                        this, "Audio Permission is necessary to start the way!"
                                    )
                                }
                            }
                        }

                        false -> {
                            binding.buttonBottom.isEnabled = false
                        }
                    }

                1 ->
                    when (isChecked) {
                        true -> {
                        }
                        false -> {
                        }
                    }
            }
        }
        binding.recyclerViewPermission.adapter = adapter

        binding.buttonBottom.setOnClickListener {
            if (binding.buttonBottom.isEnabled) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val hasPermission = permissionRequestService.isPermissionGranted(requestCode, grantResults)
        if (hasPermission) {
            binding.buttonBottom.isEnabled = true
        } else {
            binding.buttonBottom.isEnabled = false
            Util.triggerToast(this, "Audio Permission is necessary to start the way!")
        }
    }
}