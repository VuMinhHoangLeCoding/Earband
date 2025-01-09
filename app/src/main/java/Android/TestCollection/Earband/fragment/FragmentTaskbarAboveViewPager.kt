package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.databinding.FragmentTaskbarAboveViewPagerBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentTaskbarAboveViewPager : Fragment() {

    private var _binding: FragmentTaskbarAboveViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskbarAboveViewPagerBinding.inflate(inflater, container, false)


        return binding.root
    }
}