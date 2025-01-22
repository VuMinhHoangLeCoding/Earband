package Android.TestCollection.Earband.fragment.bottomNavView

import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.adapter.ViewPagerAdapter
import Android.TestCollection.Earband.databinding.MuelBottomNavHomeBinding
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@SuppressLint("SetTextI18n")
class BottomNavHome : Fragment() {

    private var _binding: MuelBottomNavHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter
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

        _binding = MuelBottomNavHomeBinding.inflate(inflater, container, false)

        viewPagerAdapter = ViewPagerAdapter(requireActivity())

        binding.toolbarButtonDrawer.setOnClickListener {
            mainDrawerHandler?.openDrawer()
        }

        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.isNestedScrollingEnabled = true

        val tabLayoutTabList = arrayListOf(
            "Audio",
            "Playlist",
            "History"
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val customTab = LayoutInflater.from(context).inflate(R.layout.muel_tab_button, null)
            val tabText = customTab.findViewById<TextView>(R.id.text_view)

            tabText.text = tabLayoutTabList[position]
            tab.customView = customTab
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {

                    }

                    1 -> {

                    }

                    2 -> {

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {

                    }

                    1 -> {

                    }

                    2 -> {

                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}