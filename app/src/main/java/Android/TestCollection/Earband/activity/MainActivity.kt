package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.FragmentListener
import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.fragment.FragmentMiniPlayer
import Android.TestCollection.Earband.fragment.FragmentPlayer
import Android.TestCollection.Earband.fragment.bottomNavView.BottomNavHome
import Android.TestCollection.Earband.fragment.bottomNavView.BottomNavOnline
import Android.TestCollection.Earband.viewModel.MainViewModel
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainDrawerHandler, FragmentListener {
    private lateinit var inputMethodManager: InputMethodManager
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var fragmentBody: FragmentContainerView
    private lateinit var fragmentBottom: FragmentContainerView
    private lateinit var fragmentFull: FragmentContainerView
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private val bottomNavHome by lazy { BottomNavHome() }
    private val bottomNavOnline by lazy { BottomNavOnline() }
    private val fragmentMiniPlayer by lazy { FragmentMiniPlayer() }
    private val fragmentPlayer by lazy { FragmentPlayer() }

    private val fragments = mapOf(
        R.id.home to bottomNavHome,
        R.id.online to bottomNavOnline
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        initializeDatabase()

        when (Util.theme) {
            "MUEL" -> {
                setContentView(R.layout.muel_activity_main)
            }
        }

        fragmentBody = findViewById(R.id.fragment_body)
        fragmentBottom = findViewById(R.id.fragment_bottom)
        fragmentFull = findViewById(R.id.fragment_full)
        bottomNavView = findViewById(R.id.bottom_nav)
        drawer = findViewById(R.id.drawer)
        drawerLayout = findViewById(R.id.drawer_layout)

        inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        mainViewModel.observableAudioHistoryEntityList.observe(this) { _ ->
            mainViewModel.setAudioHistoryList()
            mainViewModel.setCurrentAudio(mainViewModel.audioHistoryList.value!![0])
        }

        fragments.values.forEach { fragment ->
            supportFragmentManager.beginTransaction()
                .add(fragmentBody.id, fragment)
                .hide(fragment)
                .commit()
        }

        switchBottomNav(fragments[R.id.home]!!)

        supportFragmentManager.beginTransaction()
            .replace(fragmentBottom.id, fragmentMiniPlayer)
            .commit()

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    switchBottomNav(fragments[R.id.home]!!)
                    true
                }

                R.id.online -> {
                    switchBottomNav(fragments[R.id.online]!!)
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
        Log.d(TAG, "onStop called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments
        val transaction = fragmentManager.beginTransaction()

        // Iterate through the fragments and remove them
        for (fragment in fragments) {
            fragment?.let {
                transaction.remove(it)
            }
        }
        transaction.commit()

    }

    override fun onRestart() {
        Log.d(TAG, "onRestart called")
        super.onRestart()
    }

    override fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }


    override fun callbackTriggerFragmentPlayer() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_bottom, 0, 0, R.anim.to_bottom)
            .replace(fragmentFull.id, fragmentPlayer)
            .addToBackStack(null)
            .commit()
    }

    private fun switchBottomNav(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            fragments.values.forEach { hide(it) }
            show(fragment)
        }.commit()
    }

    private fun initializeDatabase() {
        mainViewModel.getAudiosFromLocal()
        mainViewModel.getHistoryAndLatestAudio()
        mainViewModel.getAppUtility()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}