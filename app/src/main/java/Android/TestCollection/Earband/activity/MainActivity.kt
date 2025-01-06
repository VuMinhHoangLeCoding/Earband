package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.application.AppAudioPlayerData
import Android.TestCollection.Earband.databinding.ActivityMainBinding
import Android.TestCollection.Earband.fragment.FragmentMainBody
import Android.TestCollection.Earband.fragment.FragmentMiniPlayer
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appAudioPlayerData: AppAudioPlayerData

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

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

        val fragmentMiniPlayer = FragmentMiniPlayer()
        supportFragmentManager.beginTransaction()
            .replace(binding.activityMainBody.fragmentContainerBottom.id, fragmentMiniPlayer)
            .commit()

        initializeData {
            initializeHistory {
                appAudioPlayerData.setSelectedAudio(mainViewModel.getAudioFromAudioHistoryOnPosition(0))
                initializePlaylist(appAudioPlayerData.getSelectedAudio().playlistId)
            }
        }
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

    private fun initializeData(onInitialize: () -> Unit) {
        mainViewModel.loadAudios()
        mainViewModel.loadObservableAudioHistoryEntityList()
        mainViewModel.observableAudioHistoryEntityList.observe(this) { _ ->
            onInitialize()
        }
    }

    private fun initializeHistory(onInitializeHistory: () -> Unit) {
        mainViewModel.loadAudioHistoryList()
        onInitializeHistory()
    }



    private fun initializePlaylist(playlistId: Long) {
        when (playlistId) {
            0L -> {
                appAudioPlayerData.setAudioPlaylist(mainViewModel.getLocalAudios())
            }
            else -> {}
        }
    }
}