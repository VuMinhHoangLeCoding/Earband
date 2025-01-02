package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.adapter.ViewPagerAdapter
import Android.TestCollection.Earband.databinding.FragmentMainBodyBinding
import Android.TestCollection.Earband.viewModel.AudioViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator


class FragmentMainBody : Fragment() {

    private var _binding: FragmentMainBodyBinding? = null
    private val binding get() = _binding!!

    private val audioViewModel: AudioViewModel by activityViewModels()

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBodyBinding.inflate(inflater, container, false)

        viewPagerAdapter = ViewPagerAdapter(requireActivity())

        binding.viewpager.adapter = viewPagerAdapter
        binding.viewpager.isNestedScrollingEnabled = true

        val tabLayoutTabList = arrayListOf(
            "Audio",
            "Video",
            "Album",
            "History"
        )

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabLayoutTabList[position]
        }.attach()

        audioViewModel.loalAudios.observe(viewLifecycleOwner) { audios ->

        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}