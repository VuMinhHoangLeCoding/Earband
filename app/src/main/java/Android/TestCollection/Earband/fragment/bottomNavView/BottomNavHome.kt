package Android.TestCollection.Earband.fragment.bottomNavView

import Android.TestCollection.Earband.MainDrawerHandler
import Android.TestCollection.Earband.adapter.ViewPagerAdapter
import Android.TestCollection.Earband.databinding.BottomNavHomeBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator


class BottomNavHome : Fragment() {

    private var _binding: BottomNavHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var mainDrawerHandler: MainDrawerHandler? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is  MainDrawerHandler) {
            mainDrawerHandler = context
        }
        else {
            throw RuntimeException("$context must implement DrawerHandler")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomNavHomeBinding.inflate(inflater, container, false)

        viewPagerAdapter = ViewPagerAdapter(requireActivity())

        binding.toolbarButtonDrawer.setOnClickListener {
            mainDrawerHandler?.openDrawer()
        }

        binding.viewpager.adapter = viewPagerAdapter
        binding.viewpager.isNestedScrollingEnabled = true

        val tabLayoutTabList = arrayListOf(
            "Audio",
            "Video",
            "Playlist",
            "History"
        )

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabLayoutTabList[position]
        }.attach()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}