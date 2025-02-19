package Android.TestCollection.Earband.ui.main

import Android.TestCollection.Earband.FragmentListener
import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.view_model.MainViewModel
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
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
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var fragmentBody: FragmentContainerView
    private lateinit var fragmentBottom: FragmentContainerView
    private lateinit var fragmentFull: FragmentContainerView
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private val bottomNavAudio by lazy { BottomNavAudio() }
    private val bottomNavOverview by lazy { BottomNavOverview() }
    private val fragmentMiniPlayer by lazy { FragmentMiniPlayer() }
    private val fragmentPlayer by lazy { FragmentPlayer() }

    private val fragments = mapOf(
        R.id.home to bottomNavAudio,
        R.id.online to bottomNavOverview
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        initializeDatabase()
        initializeTheme()
        initializeView()
        setupBottomNavView()
        setupDrawer()
        setupMiniPlayer()

        mainViewModel.observableAudioHistoryEntityList.observe(this) { _ ->
            mainViewModel.setAudioHistoryList()
            if (mainViewModel.audioHistoryList.value!!.isNotEmpty()){
                mainViewModel.setCurrentAudio(mainViewModel.audioHistoryList.value!![0])
            }
            else mainViewModel.setCurrentAudio(Audio.emptyAudio)

            if(mainViewModel.currentAudio.value == Audio.emptyAudio) fragmentBottom.visibility = View.GONE else fragmentBottom.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        super.onDestroy()
    }

    private fun initializeDatabase() {
        mainViewModel.getAudiosFromLocal()
        mainViewModel.getHistoryAndLatestAudio()
        mainViewModel.getAppUtility()
    }

    private fun initializeTheme() {
        when (Util.theme) {
            "MUEL" -> setContentView(R.layout.muel_activity_main)
            "MUD" -> setContentView(R.layout.mud_activity_main)
            else -> setContentView(R.layout.muel_activity_main)
        }
    }

    private fun initializeView() {
        fragmentBody = findViewById(R.id.fragment_body)
        fragmentBottom = findViewById(R.id.fragment_bottom)
        fragmentFull = findViewById(R.id.fragment_full)
        bottomNavView = findViewById(R.id.bottom_nav)
        drawer = findViewById(R.id.drawer)
        drawerLayout = findViewById(R.id.drawer_layout)
    }

    private fun setupBottomNavView() {
        fragments.values.forEach { fragment ->
            supportFragmentManager.beginTransaction()
                .add(fragmentBody.id, fragment)
                .hide(fragment)
                .commit()
        }
        switchBottomNav(fragments[R.id.home]!!)

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

    private fun setupDrawer() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun setupMiniPlayer() {
        supportFragmentManager.beginTransaction()
            .replace(fragmentBottom.id, fragmentMiniPlayer)
            .commit()
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

    companion object {
        private const val TAG = "MainActivity"
    }
}