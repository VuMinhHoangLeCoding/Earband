package Android.TestCollection.Earband.fragment.bottomNavView

import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.adapter.ViewPagerAdapter
import Android.TestCollection.Earband.fragment.viewPager.ViewPagerAudio
import Android.TestCollection.Earband.fragment.viewPager.ViewPagerHistory
import Android.TestCollection.Earband.fragment.viewPager.ViewPagerPlaylist
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@SuppressLint("SetTextI18n")
class BottomNavHome : Fragment() {


    private lateinit var toolbarButtonDrawer: ImageButton
    private lateinit var toolbarButtonMenu: ImageButton
    private lateinit var viewpager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var mainDrawerHandler: MainDrawerHandler? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainDrawerHandler) {
            mainDrawerHandler = context
        } else {
            throw RuntimeException("$context must implement DrawerHandler")
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_bottom_nav_home, container, false)
            else -> inflater.inflate(R.layout.muel_bottom_nav_home, container, false)
        }

        initializeView(view)
        setupViewPager()
        setupToolbarButton()

        return view
    }

    private fun initializeView(view: View) {
        toolbarButtonMenu = view.findViewById(R.id.toolbar_button_menu)
        toolbarButtonDrawer = view.findViewById(R.id.toolbar_button_drawer)
        viewpager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
    }

    private fun setupToolbarButton() {
        toolbarButtonDrawer.setOnClickListener {
            mainDrawerHandler?.openDrawer()
        }
    }

    private fun setupViewPager() {
        val tabLayoutTabList = arrayListOf(
            "Audio", "Playlist", "History"
        )
        val fragments = listOf(
            ViewPagerAudio(), ViewPagerPlaylist(), ViewPagerHistory()
        )

        viewpager.adapter = ViewPagerAdapter(requireActivity(), fragments)
        viewpager.isNestedScrollingEnabled = true

        TabLayoutMediator(tabLayout, viewpager) { tab, position ->
            val customTab = LayoutInflater.from(context).inflate(R.layout.muel_tab_button, null)
            val tabText = customTab.findViewById<TextView>(R.id.text_view)
            tabText.text = tabLayoutTabList[position]
            tab.customView = customTab
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {}
                    1 -> {}
                    2 -> {}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {}
                    1 -> {}
                    2 -> {}
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }


}