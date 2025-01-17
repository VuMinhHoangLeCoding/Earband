package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.application.AppPlayerDataModel
import Android.TestCollection.Earband.databinding.ActivityMainBinding
import Android.TestCollection.Earband.fragment.FragmentMiniPlayer
import Android.TestCollection.Earband.fragment.bottomNavView.BottomNavHome
import Android.TestCollection.Earband.fragment.bottomNavView.BottomNavOnline
import Android.TestCollection.Earband.model.Playlist
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainDrawerHandler {

    @Inject
    lateinit var appPlayerDataModel: AppPlayerDataModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var inputMethodManager: InputMethodManager
    private val mainViewModel: MainViewModel by viewModels()

    private val bottomNavHome by lazy { BottomNavHome() }
    private val bottomNavOnline by lazy { BottomNavOnline() }
    private val fragmentMiniPlayer by lazy { FragmentMiniPlayer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        initializeData {
            initializeHistory {
                appPlayerDataModel.setSelectedAudio(mainViewModel.getAudioFromAudioHistoryOnPosition(0))
                initializePlaylist(appPlayerDataModel.getSelectedAudio().playlistId)
            }
        }

        inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

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

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val view = currentFocus //currentFocus: retrieve current focus view
        if (view is TextInputEditText || view is TextInputLayout) { //check if the current view is the textbox or not
            val rect = Rect() //Rect() - rectangle - is a class for coordination of something (view / items in the  UI, ...)
            view.getGlobalVisibleRect(rect) //get the coordinate of the view, which is the view of the textbox
            if (!rect.contains(event!!.rawX.toInt(), event.rawY.toInt())) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()

                if (view is TextInputEditText) {
                    view.text?.clear()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(0, 0)
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
                appPlayerDataModel.setAudios(mainViewModel.getLocalAudios())
                appPlayerDataModel.setCurrentPlaylist(Playlist.localPlaylist)
            }

            else -> {}
        }
    }
}