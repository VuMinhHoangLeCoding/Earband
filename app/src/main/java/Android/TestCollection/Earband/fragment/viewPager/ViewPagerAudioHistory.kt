package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.databinding.ViewPagerAudioHistoryBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ViewPagerAudioHistory : Fragment() {

    private var _binding: ViewPagerAudioHistoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ViewPagerAudioHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
}