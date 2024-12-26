package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.databinding.ActivityMainBinding
import Android.TestCollection.Earband.fragment.FragmentMainBody
import Android.TestCollection.Earband.viewModel.AudioViewModel
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val audioViewModel: AudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityMainBody.toolbarButtonDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val fragmentMainBody = FragmentMainBody()
        supportFragmentManager.beginTransaction()
            .replace(binding.activityMainBody.fragmentContainer.id, fragmentMainBody)
            .commit()
        audioViewModel.loadAudios()
        audioViewModel.audios.observe(this) { audio ->

        }
    }
}