package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.databinding.ActivityMainBinding
import Android.TestCollection.Earband.fragment.FragmentMainBody
import Android.TestCollection.Earband.fragment.FragmentMiniPlayer
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.viewModel.AudioViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val audioViewModel: AudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityMainBody.toolbarButtonDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val fragmentMainBody = FragmentMainBody()
        supportFragmentManager.beginTransaction()
            .replace(binding.activityMainBody.fragmentContainer.id, fragmentMainBody)
            .commit()
        audioViewModel.getAudio(Audio.emptyAudio)
        audioViewModel.loadAudios()

        val fragmentMiniPlayer = FragmentMiniPlayer()
        supportFragmentManager.beginTransaction()
            .replace(binding.activityMainBody.fragmentContainerBottom.id, fragmentMiniPlayer)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart called")
        super.onRestart()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}