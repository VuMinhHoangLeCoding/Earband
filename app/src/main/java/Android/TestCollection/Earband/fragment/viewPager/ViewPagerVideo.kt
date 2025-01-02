package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.databinding.ViewPagerVideoBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ViewPagerVideo : Fragment() {

    private var _binding: ViewPagerVideoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ViewPagerVideoBinding.inflate(inflater, container, false)
        return binding.root
    }
}