package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.application.AudioPlayerData
import Android.TestCollection.Earband.databinding.ActivityMainBinding
import Android.TestCollection.Earband.fragment.bottomNavView.BottomNavHome
import Android.TestCollection.Earband.fragment.FragmentMiniPlayer
import Android.TestCollection.Earband.fragment.bottomNavView.BottomNavOnline
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainDrawerHandler {

    @Inject
    lateinit var audioPlayerData: AudioPlayerData

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private val bottomNavHome by lazy { BottomNavHome() }
    private val bottomNavOnline by lazy { BottomNavOnline() }
    private val fragmentMiniPlayer by lazy { FragmentMiniPlayer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        initializeData {
            initializeHistory {
                audioPlayerData.setSelectedAudio(mainViewModel.getAudioFromAudioHistoryOnPosition(0))
                initializePlaylist(audioPlayerData.getSelectedAudio().playlistId)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(bottomNavHome)

        supportFragmentManager.beginTransaction()
            .replace(binding.activityMainBody.fragmentContainerBottom.id, fragmentMiniPlayer)
            .commit()

        binding.activityMainBody.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(bottomNavHome)
                    true
                }

                R.id.online -> {
                    replaceFragment(bottomNavOnline)
                    true
                }

                else -> false

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

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(0,0)
            .replace(binding.activityMainBody.fragmentContainer.id, fragment)
            .commit()
    }

    private fun initializeData(onInitialize: () -> Unit) {
        mainViewModel.getAudiosFromLocal()
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
                audioPlayerData.setAudioPlaylist(mainViewModel.getLocalAudios())
            }

            else -> {}
        }
    }
}